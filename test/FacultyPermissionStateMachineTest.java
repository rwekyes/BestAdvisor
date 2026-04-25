import edu.advising.commands.*;
import edu.advising.contexts.FacultyPermissionContext;
import edu.advising.core.DatabaseManager;
import edu.advising.model.FacultyPermission;
import edu.advising.model.Section;
import edu.advising.model.WaitlistEntry;
import edu.advising.notifications.ObservableStudent;
import edu.advising.permissions.PermissionTreeFactory;
import edu.advising.states.StateFactory;
import edu.advising.states.facultypermissionstates.*;
import edu.advising.users.Faculty;
import edu.advising.users.Student;

import java.sql.SQLException;
import java.time.LocalDateTime;

/**
 * Test suite for the Faculty Permission State Machine.
 *
 * Unit tests bypass the DB by building FacultyPermission with id=0
 * (so persist() is a no-op) and using load() with null for faculty/entry
 * where notifications are not needed.
 *
 * Run with: mvn exec:java@run-FacultyPermissionStateMachineTest
 */
public class FacultyPermissionStateMachineTest {

    // -------------------------------------------------------------------------
    // Helpers
    // -------------------------------------------------------------------------

    private static int passed = 0;
    private static int failed = 0;

    private static void pass(String name) {
        System.out.println("  PASS  " + name);
        passed++;
    }

    private static void fail(String name, String reason) {
        System.out.println("  FAIL  " + name + " — " + reason);
        failed++;
    }

    private static void header(String section) {
        System.out.println("\n=== " + section + " ===");
    }

    /** Builds an in-memory FacultyPermission in the given status — no DB touch. */
    private static FacultyPermission makeFp(String status) {
        FacultyPermission fp = new FacultyPermission();
        fp.setStatus(status);
        fp.setRequestDate(LocalDateTime.now());
        fp.setExpiryDate(LocalDateTime.now().plusHours(48));
        return fp;
    }

    /** Builds an in-memory FacultyPermission with expiry in the past. */
    private static FacultyPermission makeExpiredFp() {
        FacultyPermission fp = new FacultyPermission();
        fp.setStatus("APPROVED");
        fp.setRequestDate(LocalDateTime.now().minusDays(3));
        fp.setExpiryDate(LocalDateTime.now().minusHours(1));
        return fp;
    }

    /** Wraps a FacultyPermission in a context. Null faculty/section/entry safe for unit tests
     *  that don't trigger notification paths. */
    private static FacultyPermissionContext makeContext(String status) {
        return FacultyPermissionContext.load(makeFp(status), null, null, null);
    }

    private static String fetchStatusFromDb(int id) throws SQLException {
        return DatabaseManager.getInstance().executeQuery(
                "SELECT status FROM faculty_permissions WHERE id = ?",
                rs -> rs.next() ? rs.getString("status") : null,
                id);
    }

    private static String fetchDenyReasonFromDb(int id) throws SQLException {
        return DatabaseManager.getInstance().executeQuery(
                "SELECT deny_reason FROM faculty_permissions WHERE id = ?",
                rs -> rs.next() ? rs.getString("deny_reason") : null,
                id);
    }

    private static LocalDateTime fetchExpiryFromDb(int id) throws SQLException {
        return DatabaseManager.getInstance().executeQuery(
                "SELECT expiry_date FROM faculty_permissions WHERE id = ?",
                rs -> {
                    if (!rs.next()) return null;
                    var ts = rs.getTimestamp("expiry_date");
                    return ts != null ? ts.toLocalDateTime() : null;
                },
                id);
    }

    // -------------------------------------------------------------------------
    // Unit Tests — StateFactory
    // -------------------------------------------------------------------------

    static void test_stateFactory_allFourStrings() {
        String name = "StateFactory maps all four status strings correctly";
        try {
            boolean ok =
                    StateFactory.facultyPermissionStateFor("REQUESTED") instanceof RequestedPermissionState
                            && StateFactory.facultyPermissionStateFor("APPROVED")  instanceof ApprovedPermissionState
                            && StateFactory.facultyPermissionStateFor("DENIED")    instanceof DeniedPermissionState
                            && StateFactory.facultyPermissionStateFor("EXPIRED")   instanceof ExpiredPermissionState;
            if (ok) pass(name);
            else fail(name, "One or more mappings returned wrong type");
        } catch (Exception ex) { fail(name, ex.getMessage()); }
    }

    // -------------------------------------------------------------------------
    // Unit Tests — Valid Transitions
    // -------------------------------------------------------------------------

    static void test_requestedApprove_transitionsToApproved() {
        String name = "REQUESTED → APPROVED via approve()";
        try {
            FacultyPermissionContext ctx = makeContext("REQUESTED");
            ctx.approve();
            boolean ok = ctx.getState() instanceof ApprovedPermissionState
                    && "APPROVED".equals(ctx.getFacultyPermission().getStatus());
            if (ok) pass(name);
            else fail(name, "state=" + ctx.getState().getClass().getSimpleName()
                    + " status=" + ctx.getFacultyPermission().getStatus());
        } catch (Exception ex) { fail(name, ex.getMessage()); }
    }

    static void test_requestedDeny_transitionsToDenied() {
        String name = "REQUESTED → DENIED via deny()";
        try {
            FacultyPermissionContext ctx = makeContext("REQUESTED");
            ctx.deny();
            boolean ok = ctx.getState() instanceof DeniedPermissionState
                    && "DENIED".equals(ctx.getFacultyPermission().getStatus());
            if (ok) pass(name);
            else fail(name, "state=" + ctx.getState().getClass().getSimpleName()
                    + " status=" + ctx.getFacultyPermission().getStatus());
        } catch (Exception ex) { fail(name, ex.getMessage()); }
    }

    static void test_approvedExpire_transitionsToExpired() {
        String name = "APPROVED → EXPIRED via expire()";
        try {
            FacultyPermissionContext ctx = makeContext("APPROVED");
            ctx.expire();
            boolean ok = ctx.getState() instanceof ExpiredPermissionState
                    && "EXPIRED".equals(ctx.getFacultyPermission().getStatus());
            if (ok) pass(name);
            else fail(name, "state=" + ctx.getState().getClass().getSimpleName()
                    + " status=" + ctx.getFacultyPermission().getStatus());
        } catch (Exception ex) { fail(name, ex.getMessage()); }
    }

    static void test_approvedRevoke_transitionsToDenied() {
        String name = "APPROVED → DENIED via revoke()";
        try {
            FacultyPermissionContext ctx = makeContext("APPROVED");
            ctx.revoke();
            boolean ok = ctx.getState() instanceof DeniedPermissionState
                    && "DENIED".equals(ctx.getFacultyPermission().getStatus());
            if (ok) pass(name);
            else fail(name, "state=" + ctx.getState().getClass().getSimpleName()
                    + " status=" + ctx.getFacultyPermission().getStatus());
        } catch (Exception ex) { fail(name, ex.getMessage()); }
    }

    static void test_deniedResubmit_transitionsToRequested() {
        String name = "DENIED → REQUESTED via resubmit()";
        try {
            // Need section for notification call in resubmit — use a minimal in-memory section
            FacultyPermission fp = makeFp("DENIED");
            Section section = new Section("001", "FALL", 2026, 30);
            FacultyPermissionContext ctx = FacultyPermissionContext.load(fp, null, section, null);

            ctx.resubmit();

            boolean stateOk  = ctx.getState() instanceof RequestedPermissionState;
            boolean statusOk = "REQUESTED".equals(ctx.getFacultyPermission().getStatus());
            boolean expiryOk = ctx.getFacultyPermission().getExpiryDate()
                    .isAfter(LocalDateTime.now().plusHours(47));

            if (stateOk && statusOk && expiryOk) pass(name);
            else fail(name, "state=" + ctx.getState().getClass().getSimpleName()
                    + " status=" + ctx.getFacultyPermission().getStatus()
                    + " expiryReset=" + expiryOk);
        } catch (Exception ex) { fail(name, ex.getMessage()); }
    }

    static void test_expiredResubmit_transitionsToRequested() {
        String name = "EXPIRED → REQUESTED via resubmit()";
        try {
            FacultyPermission fp = makeFp("EXPIRED");
            Section section = new Section("001", "FALL", 2026, 30);
            FacultyPermissionContext ctx = FacultyPermissionContext.load(fp, null, section, null);

            ctx.resubmit();

            boolean stateOk  = ctx.getState() instanceof RequestedPermissionState;
            boolean statusOk = "REQUESTED".equals(ctx.getFacultyPermission().getStatus());
            boolean expiryOk = ctx.getFacultyPermission().getExpiryDate()
                    .isAfter(LocalDateTime.now().plusHours(47));

            if (stateOk && statusOk && expiryOk) pass(name);
            else fail(name, "state=" + ctx.getState().getClass().getSimpleName()
                    + " status=" + ctx.getFacultyPermission().getStatus());
        } catch (Exception ex) { fail(name, ex.getMessage()); }
    }

    static void test_approvedCheckAndAdvance_expiresByTime() {
        String name = "APPROVED → EXPIRED via checkAndAdvance() when expiryDate is past";
        try {
            FacultyPermission fp = makeExpiredFp(); // status=APPROVED, expiry in past
            FacultyPermissionContext ctx = FacultyPermissionContext.load(fp, null, null, null);

            // load() auto-expires on construction — check state immediately
            boolean ok = ctx.getState() instanceof ExpiredPermissionState
                    && "EXPIRED".equals(ctx.getFacultyPermission().getStatus());
            if (ok) pass(name);
            else fail(name, "Expected auto-expiry on load but state="
                    + ctx.getState().getClass().getSimpleName());
        } catch (Exception ex) { fail(name, ex.getMessage()); }
    }

    // -------------------------------------------------------------------------
    // Unit Tests — isValid()
    // -------------------------------------------------------------------------

    static void test_isValid_allStates() {
        String name = "isValid() returns true only for APPROVED state";
        try {
            boolean requestedFalse = !makeContext("REQUESTED").isValid();
            boolean approvedTrue   =  makeContext("APPROVED").isValid();
            boolean deniedFalse    = !makeContext("DENIED").isValid();
            boolean expiredFalse   = !makeContext("EXPIRED").isValid();

            if (requestedFalse && approvedTrue && deniedFalse && expiredFalse) pass(name);
            else fail(name,
                    "REQUESTED=" + !requestedFalse
                            + " APPROVED=" + approvedTrue
                            + " DENIED=" + !deniedFalse
                            + " EXPIRED=" + !expiredFalse);
        } catch (Exception ex) { fail(name, ex.getMessage()); }
    }

    // -------------------------------------------------------------------------
    // Unit Tests — Illegal Transitions
    // -------------------------------------------------------------------------

    static void test_illegal_approveFromApproved() {
        String name = "Illegal: approve() from APPROVED throws and does not change state";
        try {
            FacultyPermissionContext ctx = makeContext("APPROVED");
            try { ctx.approve(); fail(name, "Expected IllegalStateException"); return; }
            catch (IllegalStateException expected) {}
            if (ctx.getState() instanceof ApprovedPermissionState) pass(name);
            else fail(name, "State changed to: " + ctx.getState().getClass().getSimpleName());
        } catch (Exception ex) { fail(name, ex.getMessage()); }
    }

    static void test_illegal_resubmitFromRequested() {
        String name = "Illegal: resubmit() from REQUESTED throws and does not change state";
        try {
            FacultyPermissionContext ctx = makeContext("REQUESTED");
            try { ctx.resubmit(); fail(name, "Expected IllegalStateException"); return; }
            catch (IllegalStateException expected) {}
            if (ctx.getState() instanceof RequestedPermissionState) pass(name);
            else fail(name, "State changed to: " + ctx.getState().getClass().getSimpleName());
        } catch (Exception ex) { fail(name, ex.getMessage()); }
    }

    static void test_illegal_allFromDenied() {
        String name = "Illegal: approve, deny, expire, revoke from DENIED all throw";
        try {
            int count = 0;
            try { makeContext("DENIED").approve(); } catch (IllegalStateException e) { count++; }
            try { makeContext("DENIED").deny();    } catch (IllegalStateException e) { count++; }
            try { makeContext("DENIED").expire();  } catch (IllegalStateException e) { count++; }
            try { makeContext("DENIED").revoke();  } catch (IllegalStateException e) { count++; }
            if (count == 4) pass(name);
            else fail(name, "Only " + count + "/4 threw IllegalStateException");
        } catch (Exception ex) { fail(name, ex.getMessage()); }
    }

    static void test_illegal_allFromExpired() {
        String name = "Illegal: approve, deny, expire, revoke from EXPIRED all throw";
        try {
            int count = 0;
            try { makeContext("EXPIRED").approve(); } catch (IllegalStateException e) { count++; }
            try { makeContext("EXPIRED").deny();    } catch (IllegalStateException e) { count++; }
            try { makeContext("EXPIRED").expire();  } catch (IllegalStateException e) { count++; }
            try { makeContext("EXPIRED").revoke();  } catch (IllegalStateException e) { count++; }
            if (count == 4) pass(name);
            else fail(name, "Only " + count + "/4 threw IllegalStateException");
        } catch (Exception ex) { fail(name, ex.getMessage()); }
    }

    // -------------------------------------------------------------------------
    // Integration Tests
    // -------------------------------------------------------------------------

    /** Seeds minimal data: student, faculty, section, waitlist entry. Returns int[]{userId, facultyId, sectionId, waitlistId}. */
    private static int[] seedMinimalData(DatabaseManager db, String suffix) throws SQLException {
        int userId = db.executeInsert(
                "INSERT INTO users (username, password, user_type, first_name, last_name, email) " +
                        "VALUES (?, 'Password1!', 'STUDENT', 'Perm', 'Student', ?)",
                "perm_student_" + suffix, "perm_" + suffix + "@test.com");
        db.executeInsert("INSERT INTO students (id, student_id) VALUES (?, ?)",
                userId, "PS" + suffix);

        int facultyUserId = db.executeInsert(
                "INSERT INTO users (username, password, user_type, first_name, last_name, email) " +
                        "VALUES (?, 'Password1!', 'FACULTY', 'Perm', 'Faculty', ?)",
                "perm_faculty_" + suffix, "pfac_" + suffix + "@test.com");
        db.executeInsert("INSERT INTO faculty (id, employee_id, department) VALUES (?, ?, 'CS')",
                facultyUserId, "EF" + suffix);

        int deptId = db.executeInsert(
                "INSERT INTO departments (code, name) VALUES (?, ?)",
                "PD" + suffix, "Perm Dept " + suffix);
        int courseId = db.executeInsert(
                "INSERT INTO courses (code, name, description, credits, department_id, level) " +
                        "VALUES (?, 'Perm Course', 'desc', 3, ?, '100')",
                "PC" + suffix, deptId);
        int sectionId = db.executeInsert(
                "INSERT INTO sections (course_id, section_number, semester, `year`, capacity, enrolled, status) " +
                        "VALUES (?, '001', 'FALL', 2094, 1, 1, 'OPEN')", courseId); // capacity=1, enrolled=1 → full

        int waitlistId = db.executeInsert(
                "INSERT INTO waitlist (student_id, section_id, position, status) VALUES (?, ?, 1, 'ACTIVE')",
                userId, sectionId);

        return new int[]{userId, facultyUserId, sectionId, waitlistId};
    }

    /**
     * Integration: create() persists REQUESTED row, approve() updates DB to APPROVED.
     */
    static void integration_createAndApprove_dbUpdated() {
        String name = "Integration: create() persists REQUESTED, approve() updates DB to APPROVED";
        try {
            DatabaseManager db = DatabaseManager.getInstance();
            int[] ids = seedMinimalData(db, "A");
            int facultyUserId = ids[1], sectionId = ids[2], waitlistId = ids[3];

            Faculty faculty = db.fetchOne(Faculty.class, "id", facultyUserId);
            Section section = db.fetchOne(Section.class, "id", sectionId);
            WaitlistEntry entry = db.fetch(
                    "SELECT * FROM waitlist WHERE id = ?",
                    rs -> { WaitlistEntry e = new WaitlistEntry(); e.setId(rs.getInt("id"));
                        e.setStudentId(rs.getInt("student_id")); e.setSectionId(rs.getInt("section_id"));
                        e.setStatus(rs.getString("status")); return e; }, waitlistId);

            FacultyPermissionContext ctx = FacultyPermissionContext.create(entry, section, faculty);
            int fpId = ctx.getFacultyPermission().getId();

            if (fpId == 0) { fail(name, "create() did not persist — id is 0"); return; }

            String statusAfterCreate = fetchStatusFromDb(fpId);
            if (!"REQUESTED".equals(statusAfterCreate)) {
                fail(name, "After create(): expected REQUESTED got " + statusAfterCreate); return;
            }

            ctx.approve();
            String statusAfterApprove = fetchStatusFromDb(fpId);
            if ("APPROVED".equals(statusAfterApprove)) pass(name);
            else fail(name, "After approve(): expected APPROVED got " + statusAfterApprove);

        } catch (Exception ex) { fail(name, ex.toString()); }
    }

    /**
     * Integration: load() an APPROVED permission with past expiryDate → auto-expires to EXPIRED in DB.
     */
    static void integration_load_autoExpiresWhenPastExpiry() {
        String name = "Integration: load() auto-expires APPROVED permission with past expiryDate";
        try {
            DatabaseManager db = DatabaseManager.getInstance();
            int[] ids = seedMinimalData(db, "B");
            int sectionId = ids[2], waitlistId = ids[3];

            // Insert an APPROVED permission with expiry in the past
            int fpId = db.executeInsert(
                    "INSERT INTO faculty_permissions (section_id, waitlist_id, request_date, expiry_date, status) " +
                            "VALUES (?, ?, ?, ?, 'APPROVED')",
                    sectionId, waitlistId,
                    LocalDateTime.now().minusDays(3),
                    LocalDateTime.now().minusHours(1));

            FacultyPermission fp = db.fetch(
                    "SELECT * FROM faculty_permissions WHERE id = ?",
                    rs -> {
                        FacultyPermission f = new FacultyPermission();
                        f.setId(rs.getInt("id"));
                        f.setSectionId(rs.getInt("section_id"));
                        f.setWaitlistId(rs.getInt("waitlist_id"));
                        f.setStatus(rs.getString("status"));
                        var exTs = rs.getTimestamp("expiry_date");
                        f.setExpiryDate(exTs != null ? exTs.toLocalDateTime() : null);
                        return f;
                    }, fpId);

            // Load with nulls — auto-expiry path skips notification when waitlistEntry is null
            FacultyPermissionContext ctx = FacultyPermissionContext.load(fp, null, null, null);

            boolean stateOk = ctx.getState() instanceof ExpiredPermissionState;
            String dbStatus = fetchStatusFromDb(fpId);
            boolean dbOk = "EXPIRED".equals(dbStatus);

            if (stateOk && dbOk) pass(name);
            else fail(name, "state=" + ctx.getState().getClass().getSimpleName()
                    + " dbStatus=" + dbStatus);
        } catch (Exception ex) { fail(name, ex.toString()); }
    }

    /**
     * Integration: RegisterCommand bypasses capacity check when student has a valid APPROVED permission.
     * Section is full (capacity=1, enrolled=1) but student has an APPROVED permission.
     */
    static void integration_registerCommand_bypassesCapacityWithValidPermission() {
        String name = "Integration: RegisterCommand succeeds on full section with valid APPROVED permission";
        try {
            DatabaseManager db = DatabaseManager.getInstance();
            int[] ids = seedMinimalData(db, "C");
            int userId = ids[0], sectionId = ids[2], waitlistId = ids[3];

            // Insert an APPROVED permission with future expiry
            db.executeInsert(
                    "INSERT INTO faculty_permissions (section_id, waitlist_id, request_date, expiry_date, status) " +
                            "VALUES (?, ?, ?, ?, 'APPROVED')",
                    sectionId, waitlistId,
                    LocalDateTime.now().minusHours(1),
                    LocalDateTime.now().plusHours(47));

            Student raw = db.fetchOne(Student.class, "id", userId);
            ObservableStudent obs = ObservableStudent.fromSuperType(raw);
            Section section = db.fetchOne(Section.class, "id", sectionId);

            // Verify section is full
            if (section.hasCapacity()) { fail(name, "Test setup error — section should be full"); return; }

            // Also need a registration period so RegisterCommand doesn't block on that
            db.executeUpdate(
                    "MERGE INTO registration_periods (semester, `year`, open_date, close_date, " +
                            "late_registration_end, current_state) KEY (semester, `year`) VALUES (?,?,?,?,?,?)",
                    "FALL", 2094,
                    LocalDateTime.now().minusDays(5),
                    LocalDateTime.now().plusDays(10),
                    LocalDateTime.now().plusDays(17),
                    "OPEN");

            RegisterCommand cmd = new RegisterCommand(obs, section, PermissionTreeFactory.forUser(obs));
            cmd.execute();

            if (cmd.wasSuccessful()) pass(name);
            else fail(name, "RegisterCommand failed — permission bypass not working. Error: "
                    + cmd.getErrorMessage());

        } catch (Exception ex) { fail(name, ex.toString()); }
    }

    /**
     * Integration: RegisterCommand is still blocked on a full section with NO valid permission.
     */
    static void integration_registerCommand_blockedWithNoPermission() {
        String name = "Integration: RegisterCommand blocked on full section with no permission";
        try {
            DatabaseManager db = DatabaseManager.getInstance();
            int[] ids = seedMinimalData(db, "D");
            int userId = ids[0], sectionId = ids[2];

            Student raw = db.fetchOne(Student.class, "id", userId);
            ObservableStudent obs = ObservableStudent.fromSuperType(raw);
            Section section = db.fetchOne(Section.class, "id", sectionId);

            db.executeUpdate(
                    "MERGE INTO registration_periods (semester, `year`, open_date, close_date, " +
                            "late_registration_end, current_state) KEY (semester, `year`) VALUES (?,?,?,?,?,?)",
                    "FALL", 2094,
                    LocalDateTime.now().minusDays(5),
                    LocalDateTime.now().plusDays(10),
                    LocalDateTime.now().plusDays(17),
                    "OPEN");

            RegisterCommand cmd = new RegisterCommand(obs, section, PermissionTreeFactory.forUser(obs));
            cmd.execute();

            if (!cmd.wasSuccessful()) pass(name);
            else fail(name, "RegisterCommand succeeded on full section with no permission");

        } catch (Exception ex) { fail(name, ex.toString()); }
    }

    // -------------------------------------------------------------------------
    // Main
    // -------------------------------------------------------------------------

    public static void main(String[] args) {

        header("Unit Tests — StateFactory");
        test_stateFactory_allFourStrings();

        header("Unit Tests — Valid Transitions");
        test_requestedApprove_transitionsToApproved();
        test_requestedDeny_transitionsToDenied();
        test_approvedExpire_transitionsToExpired();
        test_approvedRevoke_transitionsToDenied();
        test_deniedResubmit_transitionsToRequested();
        test_expiredResubmit_transitionsToRequested();
        test_approvedCheckAndAdvance_expiresByTime();

        header("Unit Tests — isValid()");
        test_isValid_allStates();

        header("Unit Tests — Illegal Transitions");
        test_illegal_approveFromApproved();
        test_illegal_resubmitFromRequested();
        test_illegal_allFromDenied();
        test_illegal_allFromExpired();

        header("Integration Tests");
        DatabaseManager db = DatabaseManager.getInstance();
        db.seedDatabase();
        integration_createAndApprove_dbUpdated();
        integration_load_autoExpiresWhenPastExpiry();
        integration_registerCommand_bypassesCapacityWithValidPermission();
        integration_registerCommand_blockedWithNoPermission();

        System.out.println("\n════════════════════════════════");
        System.out.printf("  Results: %d passed, %d failed%n", passed, failed);
        System.out.println("════════════════════════════════");

        db.shutdown();
    }
}