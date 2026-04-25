import edu.advising.commands.*;
import edu.advising.contexts.WaitlistContext;
import edu.advising.core.DatabaseManager;
import edu.advising.model.Section;
import edu.advising.model.WaitlistEntry;
import edu.advising.notifications.ObservableStudent;
import edu.advising.states.waitliststates.*;
import edu.advising.users.Student;

import java.sql.SQLException;
import java.time.LocalDateTime;

/**
 * Test suite for the Waitlist State Machine.
 *
 * Structure:
 *   Unit tests  — in-memory only, WaitlistEntry with id=0, no DB touch.
 *   Integration — seeds minimal DB data, verifies persisted rows after transitions.
 *
 * Run with: mvn exec:java@run-WaitlistStateMachineTest
 *
 * Prerequisites before running:
 *   - WaitlistEntry.setRemoveReason(String) setter added
 *   - WaitlistContext.getEntry() getter added
 *   - ActiveWaitlistState.offer(), remove(), expire() fully implemented
 *   - OfferedWaitlistState.decline(), remove(), expire() fully implemented
 */
public class WaitlistStateMachineTest {

    // -------------------------------------------------------------------------
    // Helpers
    // -------------------------------------------------------------------------

    /** Builds an in-memory WaitlistEntry in the given status — no DB touch. */
    private static WaitlistEntry makeEntry(String status) {
        WaitlistEntry e = new WaitlistEntry();
        e.setStudentId(1);
        e.setSectionId(1);
        e.setStatus(status);
        return e;
    }

    /** Builds a minimal Section with capacity so hasCapacity() returns true. */
    private static Section makeSection() {
        return new Section("001", "FALL", 2026, 30);
    }

    /** Builds a full Section with no remaining capacity. */
    private static Section makeFullSection() {
        // capacity=1, enrolled=1 → hasCapacity()=false
        return new Section(0, "001", "FALL", 2026, 1, 1, 0);
    }

    private static ObservableStudent makeStudent() {
        Student s = new Student("wl_u", "pw", "wl@u.com", "A", "B", "WL001");
        return ObservableStudent.fromSuperType(s);
    }

    private static WaitlistContext makeContext(String status) {
        return WaitlistContext.fromEntry(
                makeEntry(status),
                makeSection(),
                makeStudent(),
                new CommandExecutor(1)
        );
    }

    private static WaitlistContext makeContextWithFullSection(String status) {
        return WaitlistContext.fromEntry(
                makeEntry(status),
                makeFullSection(),
                makeStudent(),
                new CommandExecutor(1)
        );
    }

    private static String fetchStatusFromDb(int entryId) throws SQLException {
        return DatabaseManager.getInstance().executeQuery(
                "SELECT status FROM waitlist WHERE id = ?",
                rs -> rs.next() ? rs.getString("status") : null,
                entryId
        );
    }

    private static String fetchRemoveReasonFromDb(int entryId) throws SQLException {
        return DatabaseManager.getInstance().executeQuery(
                "SELECT remove_reason FROM waitlist WHERE id = ?",
                rs -> rs.next() ? rs.getString("remove_reason") : null,
                entryId
        );
    }

    private static LocalDateTime fetchRemovedDateFromDb(int entryId) throws SQLException {
        return DatabaseManager.getInstance().executeQuery(
                "SELECT removed_date FROM waitlist WHERE id = ?",
                rs -> {
                    if (!rs.next()) return null;
                    var ts = rs.getTimestamp("removed_date");
                    return ts != null ? ts.toLocalDateTime() : null;
                },
                entryId
        );
    }

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

    // -------------------------------------------------------------------------
    // Unit Tests — Valid Transitions
    // -------------------------------------------------------------------------

    /**
     * ACTIVE → OFFERED via offer(24).
     * Expects: state is OfferedWaitlistState, removedDate set as expiry,
     * status string is "OFFERED".
     */
    static void test_activeOffer_transitionsToOffered() {
        String name = "ACTIVE → OFFERED via offer(24)";
        try {
            WaitlistContext ctx = makeContext("ACTIVE");

            ctx.offer(24);

            boolean correctState  = ctx.getState() instanceof OfferedWaitlistState;
            boolean correctStatus = "OFFERED".equals(ctx.getEntry().getStatus());
            boolean expirySet     = ctx.getEntry().getRemovedDate() != null;
            // Expiry should be approximately now + 24h
            boolean expiryFuture  = ctx.getEntry().getRemovedDate()
                    .isAfter(LocalDateTime.now().plusHours(23));

            if (correctState && correctStatus && expirySet && expiryFuture) pass(name);
            else fail(name, "state=" + ctx.getState().getClass().getSimpleName()
                    + " status=" + ctx.getEntry().getStatus()
                    + " removedDate=" + ctx.getEntry().getRemovedDate());
        } catch (Exception ex) {
            fail(name, ex.getMessage());
        }
    }

    /**
     * ACTIVE → OFFERED → ENROLLED happy path via accept().
     * Expects: state is EnrolledFromWaitlistState, status is "ENROLLED".
     * Note: RegisterCommand will fail without a real DB row, so we verify
     * the state transition only — the integration test covers the full DB path.
     */
    static void test_offeredAccept_transitionsToEnrolled() {
        String name = "OFFERED → ENROLLED via accept()";
        try {
            WaitlistContext ctx = makeContext("OFFERED");

            ctx.accept();

            boolean correctState  = ctx.getState() instanceof EnrolledFromWaitlistState;
            boolean correctStatus = "ENROLLED".equals(ctx.getEntry().getStatus());

            if (correctState && correctStatus) pass(name);
            else fail(name, "state=" + ctx.getState().getClass().getSimpleName()
                    + " status=" + ctx.getEntry().getStatus());
        } catch (Exception ex) {
            fail(name, ex.getMessage());
        }
    }

    /**
     * OFFERED → REMOVED via decline().
     * Expects: state is RemovedWaitlistState, status is "REMOVED".
     */
    static void test_offeredDecline_transitionsToRemoved() {
        String name = "OFFERED → REMOVED via decline()";
        try {
            WaitlistContext ctx = makeContext("OFFERED");

            ctx.decline();

            boolean correctState  = ctx.getState() instanceof RemovedWaitlistState;
            boolean correctStatus = "REMOVED".equals(ctx.getEntry().getStatus());

            if (correctState && correctStatus) pass(name);
            else fail(name, "state=" + ctx.getState().getClass().getSimpleName()
                    + " status=" + ctx.getEntry().getStatus());
        } catch (Exception ex) {
            fail(name, ex.getMessage());
        }
    }

    /**
     * OFFERED → EXPIRED via expire().
     * Expects: state is ExpiredWaitlistState, status is "EXPIRED".
     */
    static void test_offeredExpire_transitionsToExpired() {
        String name = "OFFERED → EXPIRED via expire()";
        try {
            WaitlistContext ctx = makeContext("OFFERED");

            ctx.expire();

            boolean correctState  = ctx.getState() instanceof ExpiredWaitlistState;
            boolean correctStatus = "EXPIRED".equals(ctx.getEntry().getStatus());

            if (correctState && correctStatus) pass(name);
            else fail(name, "state=" + ctx.getState().getClass().getSimpleName()
                    + " status=" + ctx.getEntry().getStatus());
        } catch (Exception ex) {
            fail(name, ex.getMessage());
        }
    }

    /**
     * ACTIVE → REMOVED via remove(reason).
     * Expects: state is RemovedWaitlistState, removeReason is set.
     */
    static void test_activeRemove_transitionsToRemoved() {
        String name = "ACTIVE → REMOVED via remove(reason)";
        try {
            WaitlistContext ctx = makeContext("ACTIVE");

            ctx.remove("ADMIN_DROP");

            boolean correctState  = ctx.getState() instanceof RemovedWaitlistState;
            boolean correctStatus = "REMOVED".equals(ctx.getEntry().getStatus());
            boolean reasonSet     = "ADMIN_DROP".equals(ctx.getEntry().getRemoveReason());

            if (correctState && correctStatus && reasonSet) pass(name);
            else fail(name, "state=" + ctx.getState().getClass().getSimpleName()
                    + " status=" + ctx.getEntry().getStatus()
                    + " reason=" + ctx.getEntry().getRemoveReason());
        } catch (Exception ex) {
            fail(name, ex.getMessage());
        }
    }

    /**
     * ACTIVE → EXPIRED via expire().
     * Expects: state is ExpiredWaitlistState, status is "EXPIRED".
     */
    static void test_activeExpire_transitionsToExpired() {
        String name = "ACTIVE → EXPIRED via expire()";
        try {
            WaitlistContext ctx = makeContext("ACTIVE");

            ctx.expire();

            boolean correctState  = ctx.getState() instanceof ExpiredWaitlistState;
            boolean correctStatus = "EXPIRED".equals(ctx.getEntry().getStatus());

            if (correctState && correctStatus) pass(name);
            else fail(name, "state=" + ctx.getState().getClass().getSimpleName()
                    + " status=" + ctx.getEntry().getStatus());
        } catch (Exception ex) {
            fail(name, ex.getMessage());
        }
    }

    /**
     * Race condition: accept() when section has no capacity → EXPIRED.
     * Expects: state is ExpiredWaitlistState, not EnrolledFromWaitlistState.
     */
    static void test_offeredAccept_noCapacity_transitionsToExpired() {
        String name = "OFFERED accept() with no capacity → EXPIRED (race condition guard)";
        try {
            WaitlistContext ctx = makeContextWithFullSection("OFFERED");

            ctx.accept();

            boolean correctState  = ctx.getState() instanceof ExpiredWaitlistState;
            boolean correctStatus = "EXPIRED".equals(ctx.getEntry().getStatus());

            if (correctState && correctStatus) pass(name);
            else fail(name, "Expected EXPIRED but got: "
                    + ctx.getState().getClass().getSimpleName()
                    + " / " + ctx.getEntry().getStatus());
        } catch (Exception ex) {
            fail(name, ex.getMessage());
        }
    }

    // -------------------------------------------------------------------------
    // Unit Tests — isActivelyWaiting guard
    // -------------------------------------------------------------------------

    static void test_isActivelyWaiting_allStates() {
        String name = "isActivelyWaiting() returns correct value per state";
        try {
            boolean activeOk   = makeContext("ACTIVE").isActivelyWaiting();
            boolean offeredOk  = makeContext("OFFERED").isActivelyWaiting();
            boolean enrolledOk = !makeContext("ENROLLED").isActivelyWaiting();
            boolean removedOk  = !makeContext("REMOVED").isActivelyWaiting();
            boolean expiredOk  = !makeContext("EXPIRED").isActivelyWaiting();

            if (activeOk && offeredOk && enrolledOk && removedOk && expiredOk) pass(name);
            else fail(name,
                    "ACTIVE=" + makeContext("ACTIVE").isActivelyWaiting()
                            + " OFFERED=" + makeContext("OFFERED").isActivelyWaiting()
                            + " ENROLLED=" + makeContext("ENROLLED").isActivelyWaiting()
                            + " REMOVED=" + makeContext("REMOVED").isActivelyWaiting()
                            + " EXPIRED=" + makeContext("EXPIRED").isActivelyWaiting());
        } catch (Exception ex) {
            fail(name, ex.getMessage());
        }
    }

    // -------------------------------------------------------------------------
    // Unit Tests — Illegal Transitions
    // -------------------------------------------------------------------------

    static void test_illegal_acceptFromActive() {
        String name = "Illegal: accept() from ACTIVE does not change state";
        try {
            WaitlistContext ctx = makeContext("ACTIVE");
            try {
                ctx.accept();
                fail(name, "Expected IllegalStateException but none thrown");
                return;
            } catch (IllegalStateException expected) {}

            if (ctx.getState() instanceof ActiveWaitlistState
                    && "ACTIVE".equals(ctx.getEntry().getStatus())) pass(name);
            else fail(name, "State changed: " + ctx.getState().getClass().getSimpleName());
        } catch (Exception ex) {
            fail(name, ex.getMessage());
        }
    }

    static void test_illegal_declineFromActive() {
        String name = "Illegal: decline() from ACTIVE does not change state";
        try {
            WaitlistContext ctx = makeContext("ACTIVE");
            try {
                ctx.decline();
                fail(name, "Expected IllegalStateException but none thrown");
                return;
            } catch (IllegalStateException expected) {}

            if (ctx.getState() instanceof ActiveWaitlistState) pass(name);
            else fail(name, "State changed: " + ctx.getState().getClass().getSimpleName());
        } catch (Exception ex) {
            fail(name, ex.getMessage());
        }
    }

    static void test_illegal_offerFromOffered() {
        String name = "Illegal: offer() from OFFERED does not change state";
        try {
            WaitlistContext ctx = makeContext("OFFERED");
            try {
                ctx.offer(24);
                fail(name, "Expected IllegalStateException but none thrown");
                return;
            } catch (IllegalStateException expected) {}

            if (ctx.getState() instanceof OfferedWaitlistState) pass(name);
            else fail(name, "State changed: " + ctx.getState().getClass().getSimpleName());
        } catch (Exception ex) {
            fail(name, ex.getMessage());
        }
    }

    static void test_illegal_anyTransitionFromEnrolled() {
        String name = "Illegal: all transitions from ENROLLED throw";
        try {
            int throwCount = 0;
            for (Runnable action : new Runnable[]{
                    () -> makeContext("ENROLLED").offer(24),
                    () -> makeContext("ENROLLED").accept(),
                    () -> makeContext("ENROLLED").decline(),
                    () -> makeContext("ENROLLED").remove("X"),
                    () -> makeContext("ENROLLED").expire()
            }) {
                try { action.run(); }
                catch (IllegalStateException e) { throwCount++; }
            }
            if (throwCount == 5) pass(name);
            else fail(name, "Only " + throwCount + "/5 transitions threw IllegalStateException");
        } catch (Exception ex) {
            fail(name, ex.getMessage());
        }
    }

    static void test_illegal_anyTransitionFromRemoved() {
        String name = "Illegal: all transitions from REMOVED throw";
        try {
            int throwCount = 0;
            for (Runnable action : new Runnable[]{
                    () -> makeContext("REMOVED").offer(24),
                    () -> makeContext("REMOVED").accept(),
                    () -> makeContext("REMOVED").decline(),
                    () -> makeContext("REMOVED").remove("X"),
                    () -> makeContext("REMOVED").expire()
            }) {
                try { action.run(); }
                catch (IllegalStateException e) { throwCount++; }
            }
            if (throwCount == 5) pass(name);
            else fail(name, "Only " + throwCount + "/5 transitions threw IllegalStateException");
        } catch (Exception ex) {
            fail(name, ex.getMessage());
        }
    }

    static void test_illegal_anyTransitionFromExpired() {
        String name = "Illegal: all transitions from EXPIRED throw";
        try {
            int throwCount = 0;
            for (Runnable action : new Runnable[]{
                    () -> makeContext("EXPIRED").offer(24),
                    () -> makeContext("EXPIRED").accept(),
                    () -> makeContext("EXPIRED").decline(),
                    () -> makeContext("EXPIRED").remove("X"),
                    () -> makeContext("EXPIRED").expire()
            }) {
                try { action.run(); }
                catch (IllegalStateException e) { throwCount++; }
            }
            if (throwCount == 5) pass(name);
            else fail(name, "Only " + throwCount + "/5 transitions threw IllegalStateException");
        } catch (Exception ex) {
            fail(name, ex.getMessage());
        }
    }

    // -------------------------------------------------------------------------
    // Integration Tests
    // -------------------------------------------------------------------------

    /**
     * Seeds a student + full-capacity section with one waiting slot, offers
     * the seat, student accepts — verifies ENROLLED in DB and enrollment row created.
     */
    static void integration_offerAccept_enrollmentCreatedInDb() {
        String name = "Integration: offer → accept → ENROLLED in DB, enrollment created";
        try {
            DatabaseManager db = DatabaseManager.getInstance();

            // Seed user + student
            int userId = db.executeInsert(
                    "INSERT INTO users (username, password, user_type, first_name, last_name, email) " +
                            "VALUES ('wl_accept', 'Password1!', 'STUDENT', 'Wait', 'Accept', 'wl_accept@test.com')");
            db.executeInsert("INSERT INTO students (id, student_id) VALUES (?, 'WLA001')", userId);

            // Seed dept + course + section (capacity=5, enrolled=3 → hasCapacity=true)
            int deptId = db.executeInsert(
                    "INSERT INTO departments (code, name) VALUES ('WLA', 'WL Accept Dept')");
            int courseId = db.executeInsert(
                    "INSERT INTO courses (code, name, description, credits, department_id, level) " +
                            "VALUES ('WLA101', 'WL Accept Course', 'desc', 3, ?, '100')", deptId);
            int sectionId = db.executeInsert(
                    "INSERT INTO sections (course_id, section_number, semester, `year`, capacity, enrolled, status) " +
                            "VALUES (?, '001', 'FALL', 2026, 5, 3, 'OPEN')", courseId);

            // Seed a waitlist entry in ACTIVE state
            int entryId = db.executeInsert(
                    "INSERT INTO waitlist (student_id, section_id, position, status) " +
                            "VALUES (?, ?, 1, 'ACTIVE')", userId, sectionId);

            // Fetch domain objects
            Student rawStudent = db.fetchOne(Student.class, "id", userId);
            ObservableStudent obs = ObservableStudent.fromSuperType(rawStudent);
            Section section = db.fetchOne(Section.class, "id", sectionId);

            WaitlistEntry entry = db.fetch(
                    "SELECT * FROM waitlist WHERE id = ?",
                    rs -> {
                        WaitlistEntry e = new WaitlistEntry();
                        e.setId(rs.getInt("id"));
                        e.setStudentId(rs.getInt("student_id"));
                        e.setSectionId(rs.getInt("section_id"));
                        e.setStatus(rs.getString("status"));
                        return e;
                    }, entryId);

            CommandExecutor executor = new CommandExecutor(userId);
            WaitlistContext ctx = WaitlistContext.fromEntry(entry, section, obs, executor);

            // ACT: offer then accept
            ctx.offer(24);
            String statusAfterOffer = fetchStatusFromDb(entryId);
            if (!"OFFERED".equals(statusAfterOffer)) {
                fail(name, "Expected OFFERED after offer(), got: " + statusAfterOffer);
                return;
            }

            ctx.accept();

            // Verify waitlist entry is ENROLLED
            String statusAfterAccept = fetchStatusFromDb(entryId);

            // Verify an enrollment row was created
            Integer enrollmentCount = db.executeQuery(
                    "SELECT COUNT(*) FROM enrollments WHERE student_id = ? AND section_id = ?",
                    rs -> rs.next() ? rs.getInt(1) : 0,
                    userId, sectionId);

            boolean waitlistOk   = "ENROLLED".equals(statusAfterAccept);
            boolean enrollmentOk = enrollmentCount != null && enrollmentCount > 0;

            if (waitlistOk && enrollmentOk) pass(name);
            else fail(name, "waitlist status=" + statusAfterAccept
                    + " enrollments=" + enrollmentCount);

        } catch (Exception ex) {
            fail(name, ex.toString());
        }
    }

    /**
     * Seeds two waitlisted students. First student declines.
     * Verifies first entry is REMOVED and second student is promoted to OFFERED.
     */
    static void integration_decline_nextStudentPromoted() {
        String name = "Integration: student declines, next student on waitlist promoted to OFFERED";
        try {
            DatabaseManager db = DatabaseManager.getInstance();

            // Seed two students
            int userId1 = db.executeInsert(
                    "INSERT INTO users (username, password, user_type, first_name, last_name, email) " +
                            "VALUES ('wl_decline1', 'Password1!', 'STUDENT', 'Dec', 'One', 'wld1@test.com')");
            db.executeInsert("INSERT INTO students (id, student_id) VALUES (?, 'WLD001')", userId1);

            int userId2 = db.executeInsert(
                    "INSERT INTO users (username, password, user_type, first_name, last_name, email) " +
                            "VALUES ('wl_decline2', 'Password1!', 'STUDENT', 'Dec', 'Two', 'wld2@test.com')");
            db.executeInsert("INSERT INTO students (id, student_id) VALUES (?, 'WLD002')", userId2);

            // Seed section (capacity=2, enrolled=2 → full, so offer goes to waitlist)
            int deptId = db.executeInsert(
                    "INSERT INTO departments (code, name) VALUES ('WLD', 'WL Decline Dept')");
            int courseId = db.executeInsert(
                    "INSERT INTO courses (code, name, description, credits, department_id, level) " +
                            "VALUES ('WLD101', 'WL Decline Course', 'desc', 3, ?, '100')", deptId);
            int sectionId = db.executeInsert(
                    "INSERT INTO sections (course_id, section_number, semester, `year`, capacity, enrolled, status) " +
                            "VALUES (?, '001', 'FALL', 2026, 3, 2, 'OPEN')", courseId);

            // Two waitlist entries — student1 at position 1, student2 at position 2
            int entry1Id = db.executeInsert(
                    "INSERT INTO waitlist (student_id, section_id, position, status) " +
                            "VALUES (?, ?, 1, 'OFFERED')", userId1, sectionId);
            int entry2Id = db.executeInsert(
                    "INSERT INTO waitlist (student_id, section_id, position, status) " +
                            "VALUES (?, ?, 2, 'ACTIVE')", userId2, sectionId);

            Student raw1 = db.fetchOne(Student.class, "id", userId1);
            ObservableStudent obs1 = ObservableStudent.fromSuperType(raw1);
            Section section = db.fetchOne(Section.class, "id", sectionId);

            WaitlistEntry entry1 = db.fetch(
                    "SELECT * FROM waitlist WHERE id = ?",
                    rs -> {
                        WaitlistEntry e = new WaitlistEntry();
                        e.setId(rs.getInt("id"));
                        e.setStudentId(rs.getInt("student_id"));
                        e.setSectionId(rs.getInt("section_id"));
                        e.setStatus(rs.getString("status"));
                        return e;
                    }, entry1Id);

            WaitlistContext ctx1 = WaitlistContext.fromEntry(
                    entry1, section, obs1, new CommandExecutor(userId1));

            // ACT: student1 declines
            ctx1.decline();

            // Verify student1 is REMOVED
            String status1 = fetchStatusFromDb(entry1Id);

            // The decline() implementation should promote student2 — offer them the seat.
            // If promotion is not yet implemented in decline(), this will fail and guide you.
            String status2 = fetchStatusFromDb(entry2Id);

            boolean declinedOk = "REMOVED".equals(status1);
            boolean promotedOk = "OFFERED".equals(status2);

            if (declinedOk && promotedOk) pass(name);
            else fail(name, "student1=" + status1 + " student2=" + status2
                    + " (if student2 is still ACTIVE, promote-on-decline is not yet implemented in decline())");

        } catch (Exception ex) {
            fail(name, ex.toString());
        }
    }

    // -------------------------------------------------------------------------
    // Main
    // -------------------------------------------------------------------------

    public static void main(String[] args) {

        // ── Unit: valid transitions ──────────────────────────────────────────
        header("Unit Tests — Valid Transitions");
        test_activeOffer_transitionsToOffered();
        test_offeredAccept_transitionsToEnrolled();
        test_offeredDecline_transitionsToRemoved();
        test_offeredExpire_transitionsToExpired();
        test_activeRemove_transitionsToRemoved();
        test_activeExpire_transitionsToExpired();
        test_offeredAccept_noCapacity_transitionsToExpired();

        // ── Unit: isActivelyWaiting ──────────────────────────────────────────
        header("Unit Tests — isActivelyWaiting");
        test_isActivelyWaiting_allStates();

        // ── Unit: illegal transitions ────────────────────────────────────────
        header("Unit Tests — Illegal Transitions");
        test_illegal_acceptFromActive();
        test_illegal_declineFromActive();
        test_illegal_offerFromOffered();
        test_illegal_anyTransitionFromEnrolled();
        test_illegal_anyTransitionFromRemoved();
        test_illegal_anyTransitionFromExpired();

        // ── Integration ──────────────────────────────────────────────────────
        header("Integration Tests");
        DatabaseManager db = DatabaseManager.getInstance();
        db.seedDatabase();
        integration_offerAccept_enrollmentCreatedInDb();
        integration_decline_nextStudentPromoted();

        // ── Summary ──────────────────────────────────────────────────────────
        System.out.println("\n════════════════════════════════");
        System.out.printf("  Results: %d passed, %d failed%n", passed, failed);
        System.out.println("════════════════════════════════");

        db.shutdown();
    }
}