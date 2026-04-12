import edu.advising.commands.RegisterCommand;
import edu.advising.commands.Section;
import edu.advising.contexts.RegistrationPeriodContext;
import edu.advising.core.DatabaseManager;
import edu.advising.notifications.ObservableStudent;
import edu.advising.states.StateFactory;
import edu.advising.states.registrationstates.*;
import edu.advising.users.Student;

import java.sql.SQLException;
import java.time.LocalDateTime;

/**
 * Test suite for the Registration Period State Machine.
 *
 * REMAINING FIXES NEEDED (from the pre-test review):
 *
 *   Fix #1 — Constructor must initialize state from StateFactory:
 *     private RegistrationPeriodContext(RegistrationPeriod registrationPeriod) {
 *         this.registrationPeriod = registrationPeriod;
 *         this.notificationManager = NotificationManager.getInstance();
 *         this.state = StateFactory.registrationStateFor(registrationPeriod.getCurrentState());
 *     }
 *   Without this, forPeriod() returns a context with state=null, and any method
 *   call on it throws NullPointerException.
 *
 *   Fix #2 — setState() must update the entity and must NOT call persist():
 *     public void setState(RegistrationState newState) {
 *         this.state = newState;
 *         this.registrationPeriod.setCurrentState(newState.getStatusName());
 *         // no persist() here
 *     }
 *   Without the setCurrentState() call, persist() writes stale status back to DB.
 *   Without removing persist(), checkAndAdvance() causes multiple unnecessary DB writes.
 *
 * Run with: mvn exec:java@run-RegistrationPeriodStateMachineTest
 */
public class RegistrationPeriodStateMachineTest {

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

    /** Seeds a registration_periods row using MERGE so re-runs don't fail on UNIQUE. */
    private static int seedPeriod(DatabaseManager db, String semester, int year,
                                  LocalDateTime openDate, LocalDateTime closeDate,
                                  LocalDateTime lateEnd, String status) throws SQLException {
        db.executeUpdate(
                "MERGE INTO registration_periods (semester, `year`, open_date, close_date, " +
                        "late_registration_end, current_state) KEY (semester, `year`) VALUES (?,?,?,?,?,?)",
                semester, year, openDate, closeDate, lateEnd, status);
        return db.executeQuery(
                "SELECT id FROM registration_periods WHERE semester = ? AND `year` = ?",
                rs -> rs.next() ? rs.getInt(1) : 0,
                semester, year);
    }

    // -------------------------------------------------------------------------
    // Unit Tests — StateFactory
    // -------------------------------------------------------------------------

    static void test_stateFactory_allFourStrings() {
        String name = "StateFactory maps all four status strings correctly";
        try {
            boolean ok =
                    StateFactory.registrationStateFor("NOT_OPEN") instanceof NotOpenRegistrationState
                            && StateFactory.registrationStateFor("OPEN")     instanceof OpenRegistrationState
                            && StateFactory.registrationStateFor("LATE")     instanceof LateRegistrationState
                            && StateFactory.registrationStateFor("CLOSED")   instanceof ClosedRegistrationState;
            if (ok) pass(name);
            else fail(name, "One or more mappings returned wrong type");
        } catch (Exception ex) { fail(name, ex.getMessage()); }
    }

    // -------------------------------------------------------------------------
    // Unit Tests — Guard Methods via state instances directly
    // (These bypass the context constructor so they work even without fix #1)
    // -------------------------------------------------------------------------

    static void test_canRegister_perState() {
        String name = "canRegister() returns correct boolean for all four states";
        try {
            boolean notOpenFalse = !StateFactory.registrationStateFor("NOT_OPEN").canRegister(null);
            boolean openTrue     =  StateFactory.registrationStateFor("OPEN").canRegister(null);
            boolean lateTrue     =  StateFactory.registrationStateFor("LATE").canRegister(null);
            boolean closedFalse  = !StateFactory.registrationStateFor("CLOSED").canRegister(null);
            if (notOpenFalse && openTrue && lateTrue && closedFalse) pass(name);
            else fail(name, "NOT_OPEN=" + !notOpenFalse + " OPEN=" + openTrue
                    + " LATE=" + lateTrue + " CLOSED=" + !closedFalse);
        } catch (Exception ex) { fail(name, ex.getMessage()); }
    }

    static void test_canDrop_perState() {
        String name = "canDrop() returns correct boolean for all four states";
        try {
            boolean notOpenFalse = !StateFactory.registrationStateFor("NOT_OPEN").canDrop(null);
            boolean openTrue     =  StateFactory.registrationStateFor("OPEN").canDrop(null);
            boolean lateTrue     =  StateFactory.registrationStateFor("LATE").canDrop(null);
            boolean closedFalse  = !StateFactory.registrationStateFor("CLOSED").canDrop(null);
            if (notOpenFalse && openTrue && lateTrue && closedFalse) pass(name);
            else fail(name, "NOT_OPEN=" + !notOpenFalse + " OPEN=" + openTrue
                    + " LATE=" + lateTrue + " CLOSED=" + !closedFalse);
        } catch (Exception ex) { fail(name, ex.getMessage()); }
    }

    static void test_isOpen_perState() {
        String name = "isOpen() returns correct boolean for all four states";
        try {
            boolean notOpenFalse = !StateFactory.registrationStateFor("NOT_OPEN").isOpen(null);
            boolean openTrue     =  StateFactory.registrationStateFor("OPEN").isOpen(null);
            boolean lateTrue     =  StateFactory.registrationStateFor("LATE").isOpen(null);
            boolean closedFalse  = !StateFactory.registrationStateFor("CLOSED").isOpen(null);
            if (notOpenFalse && openTrue && lateTrue && closedFalse) pass(name);
            else fail(name, "NOT_OPEN=" + !notOpenFalse + " OPEN=" + openTrue
                    + " LATE=" + lateTrue + " CLOSED=" + !closedFalse);
        } catch (Exception ex) { fail(name, ex.getMessage()); }
    }

    // -------------------------------------------------------------------------
    // Unit Tests — Illegal Transitions
    // -------------------------------------------------------------------------

    static void test_illegal_transitions() {
        String name = "Illegal transitions throw IllegalStateException";
        try {
            int count = 0;
            // open() from OPEN and CLOSED
            try { StateFactory.registrationStateFor("OPEN").open(null); }
            catch (IllegalStateException e) { count++; }
            try { StateFactory.registrationStateFor("CLOSED").open(null); }
            catch (IllegalStateException e) { count++; }
            // transitionToLate() from NOT_OPEN and LATE
            try { StateFactory.registrationStateFor("NOT_OPEN").transitionToLate(null); }
            catch (IllegalStateException e) { count++; }
            try { StateFactory.registrationStateFor("LATE").transitionToLate(null); }
            catch (IllegalStateException e) { count++; }
            // close() from NOT_OPEN and CLOSED
            try { StateFactory.registrationStateFor("NOT_OPEN").close(null); }
            catch (IllegalStateException e) { count++; }
            try { StateFactory.registrationStateFor("CLOSED").close(null); }
            catch (IllegalStateException e) { count++; }

            if (count == 6) pass(name);
            else fail(name, "Only " + count + "/6 illegal transitions threw IllegalStateException");
        } catch (Exception ex) { fail(name, ex.getMessage()); }
    }

    // -------------------------------------------------------------------------
    // Integration Tests
    // -------------------------------------------------------------------------

    /**
     * forPeriod() loads a LATE period from DB and reconstructs LateRegistrationState.
     * Requires fix #1.
     */
    static void integration_forPeriod_loadsCorrectState() {
        String name = "Integration: forPeriod() reconstructs correct state from DB (requires fix #1)";
        try {
            DatabaseManager db = DatabaseManager.getInstance();
            LocalDateTime now = LocalDateTime.now();
            seedPeriod(db, "FALL", 2090,
                    now.minusDays(15), now.minusDays(5), now.plusDays(3), "LATE");

            RegistrationPeriodContext ctx = RegistrationPeriodContext.forPeriod("FALL", 2090);
            if (ctx == null) { fail(name, "forPeriod() returned null"); return; }

            boolean ok = ctx.getState() instanceof LateRegistrationState
                    && "LATE".equals(ctx.getStateName());
            if (ok) pass(name);
            else fail(name, ctx.getState() == null
                    ? "state is null — fix #1 (constructor initializes state) not applied"
                    : "Expected LateRegistrationState, got: " + ctx.getState().getClass().getSimpleName());
        } catch (NullPointerException npe) {
            fail(name, "NullPointerException — fix #1 not applied");
        } catch (Exception ex) { fail(name, ex.toString()); }
    }

    /**
     * currentPeriod() auto-advances NOT_OPEN → OPEN when openDate is past.
     * Verifies in-memory state and DB row. Requires fixes #1 and #2.
     */
    static void integration_currentPeriod_autoAdvancesToOpen() {
        String name = "Integration: currentPeriod() auto-advances NOT_OPEN → OPEN, DB updated";
        try {
            DatabaseManager db = DatabaseManager.getInstance();
            LocalDateTime now = LocalDateTime.now();
            int id = seedPeriod(db, "SPRING", 2091,
                    now.minusDays(5), now.plusDays(10), now.plusDays(17), "NOT_OPEN");

            RegistrationPeriodContext ctx = RegistrationPeriodContext.currentPeriod("SPRING", 2091);
            if (ctx == null) { fail(name, "currentPeriod() returned null"); return; }

            boolean stateOk = ctx.getState() instanceof OpenRegistrationState;
            String dbStatus = db.executeQuery(
                    "SELECT current_state FROM registration_periods WHERE id = ?",
                    rs -> rs.next() ? rs.getString(1) : null, id);
            boolean dbOk = "OPEN".equals(dbStatus);

            if (stateOk && dbOk) pass(name);
            else fail(name, "state=" + ctx.getState().getClass().getSimpleName()
                    + " dbStatus=" + dbStatus
                    + (dbOk ? "" : " — fix #2 (setState calls setCurrentState) not applied"));
        } catch (NullPointerException npe) {
            fail(name, "NullPointerException — fix #1 not applied");
        } catch (Exception ex) { fail(name, ex.toString()); }
    }

    /**
     * currentPeriod() auto-advances NOT_OPEN → LATE when past closeDate but lateEnd future.
     */
    static void integration_currentPeriod_autoAdvancesToLate() {
        String name = "Integration: currentPeriod() auto-advances NOT_OPEN → LATE, DB updated";
        try {
            DatabaseManager db = DatabaseManager.getInstance();
            LocalDateTime now = LocalDateTime.now();
            int id = seedPeriod(db, "WINTER", 2091,
                    now.minusDays(15), now.minusDays(5), now.plusDays(3), "NOT_OPEN");

            RegistrationPeriodContext ctx = RegistrationPeriodContext.currentPeriod("WINTER", 2091);
            if (ctx == null) { fail(name, "currentPeriod() returned null"); return; }

            boolean stateOk = ctx.getState() instanceof LateRegistrationState;
            String dbStatus = db.executeQuery(
                    "SELECT current_state FROM registration_periods WHERE id = ?",
                    rs -> rs.next() ? rs.getString(1) : null, id);
            boolean dbOk = "LATE".equals(dbStatus);

            if (stateOk && dbOk) pass(name);
            else fail(name, "state=" + ctx.getState().getClass().getSimpleName()
                    + " dbStatus=" + dbStatus);
        } catch (NullPointerException npe) {
            fail(name, "NullPointerException — fix #1 not applied");
        } catch (Exception ex) { fail(name, ex.toString()); }
    }

    /**
     * currentPeriod() auto-advances all the way to CLOSED when all dates are past.
     */
    static void integration_currentPeriod_autoAdvancesToClosed() {
        String name = "Integration: currentPeriod() auto-advances NOT_OPEN → CLOSED when all dates past";
        try {
            DatabaseManager db = DatabaseManager.getInstance();
            LocalDateTime now = LocalDateTime.now();
            int id = seedPeriod(db, "SUMMER", 2091,
                    now.minusDays(30), now.minusDays(20), now.minusDays(13), "NOT_OPEN");

            RegistrationPeriodContext ctx = RegistrationPeriodContext.currentPeriod("SUMMER", 2091);
            if (ctx == null) { fail(name, "currentPeriod() returned null"); return; }

            boolean stateOk = ctx.getState() instanceof ClosedRegistrationState;
            String dbStatus = db.executeQuery(
                    "SELECT current_state FROM registration_periods WHERE id = ?",
                    rs -> rs.next() ? rs.getString(1) : null, id);
            boolean dbOk = "CLOSED".equals(dbStatus);

            if (stateOk && dbOk) pass(name);
            else fail(name, "state=" + ctx.getState().getClass().getSimpleName()
                    + " dbStatus=" + dbStatus);
        } catch (NullPointerException npe) {
            fail(name, "NullPointerException — fix #1 not applied");
        } catch (Exception ex) { fail(name, ex.toString()); }
    }

    /**
     * forPeriod() returns null gracefully for a non-existent semester/year.
     */
    static void integration_forPeriod_returnsNullForMissingPeriod() {
        String name = "Integration: forPeriod() returns null for non-existent period";
        try {
            RegistrationPeriodContext ctx = RegistrationPeriodContext.forPeriod("ATLANTIS", 9999);
            if (ctx == null) pass(name);
            else fail(name, "Expected null but got a context");
        } catch (Exception ex) { fail(name, ex.toString()); }
    }

    /**
     * canRegister() and canDrop() return true via context for an OPEN period.
     */
    static void integration_guardMethods_openPeriod() {
        String name = "Integration: canRegister() and canDrop() return true for OPEN period via context";
        try {
            DatabaseManager db = DatabaseManager.getInstance();
            LocalDateTime now = LocalDateTime.now();
            seedPeriod(db, "FALL", 2092,
                    now.minusDays(5), now.plusDays(10), now.plusDays(17), "OPEN");

            RegistrationPeriodContext ctx = RegistrationPeriodContext.forPeriod("FALL", 2092);
            if (ctx == null) { fail(name, "forPeriod() returned null"); return; }

            boolean ok = ctx.canRegister() && ctx.canDrop();
            if (ok) pass(name);
            else fail(name, "canRegister=" + ctx.canRegister() + " canDrop=" + ctx.canDrop());
        } catch (NullPointerException npe) {
            fail(name, "NullPointerException — fix #1 not applied");
        } catch (Exception ex) { fail(name, ex.toString()); }
    }

    /**
     * RegisterCommand.execute() fails the pre-condition check when the period is CLOSED.
     */
    static void integration_registerCommand_blockedWhenClosed() {
        String name = "Integration: RegisterCommand blocked when period is CLOSED";
        try {
            DatabaseManager db = DatabaseManager.getInstance();
            LocalDateTime now = LocalDateTime.now();

            // Seed a CLOSED period for FALL 2093
            seedPeriod(db, "FALL", 2093,
                    now.minusDays(30), now.minusDays(20), now.minusDays(13), "CLOSED");

            // Seed student
            int userId = db.executeInsert(
                    "INSERT INTO users (username, password, user_type, first_name, last_name, email) " +
                            "VALUES ('rp_student', 'Password1!', 'STUDENT', 'Reg', 'Period', 'rp@test.com')");
            db.executeInsert("INSERT INTO students (id, student_id) VALUES (?, 'RP001')", userId);

            // Seed dept + course + section for FALL 2093
            int deptId = db.executeInsert(
                    "INSERT INTO departments (code, name) VALUES ('RP', 'Reg Period Dept')");
            int courseId = db.executeInsert(
                    "INSERT INTO courses (code, name, description, credits, department_id, level) " +
                            "VALUES ('RP101', 'Reg Period Course', 'desc', 3, ?, '100')", deptId);
            int sectionId = db.executeInsert(
                    "INSERT INTO sections (course_id, section_number, semester, `year`, " +
                            "capacity, enrolled, status) VALUES (?, '001', 'FALL', 2093, 30, 0, 'OPEN')",
                    courseId);

            Student raw = db.fetchOne(Student.class, "id", userId);
            ObservableStudent obs = ObservableStudent.fromSuperType(raw);
            Section section = db.fetchOne(Section.class, "id", sectionId);

            RegisterCommand cmd = new RegisterCommand(obs, section);
            cmd.execute();

            if (!cmd.wasSuccessful()) pass(name);
            else fail(name, "RegisterCommand succeeded — period pre-condition check not working");

        } catch (Exception ex) { fail(name, ex.toString()); }
    }

    // -------------------------------------------------------------------------
    // Main
    // -------------------------------------------------------------------------

    public static void main(String[] args) {

        header("Unit Tests — StateFactory");
        test_stateFactory_allFourStrings();

        header("Unit Tests — Guard Methods");
        test_canRegister_perState();
        test_canDrop_perState();
        test_isOpen_perState();

        header("Unit Tests — Illegal Transitions");
        test_illegal_transitions();

        header("Integration Tests");
        DatabaseManager db = DatabaseManager.getInstance();
        db.seedDatabase();
        integration_forPeriod_loadsCorrectState();
        integration_currentPeriod_autoAdvancesToOpen();
        integration_currentPeriod_autoAdvancesToLate();
        integration_currentPeriod_autoAdvancesToClosed();
        integration_forPeriod_returnsNullForMissingPeriod();
        integration_guardMethods_openPeriod();
        integration_registerCommand_blockedWhenClosed();

        System.out.println("\n════════════════════════════════");
        System.out.printf("  Results: %d passed, %d failed%n", passed, failed);
        System.out.println("════════════════════════════════");

        db.shutdown();
    }
}