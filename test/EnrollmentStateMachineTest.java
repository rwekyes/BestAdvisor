
import edu.advising.commands.Enrollment;
import edu.advising.commands.Section;
import edu.advising.contexts.EnrollmentContext;
import edu.advising.core.DatabaseManager;
import edu.advising.states.enrollmentstates.CompletedEnrollmentState;
import edu.advising.states.enrollmentstates.DroppedEnrollmentState;
import edu.advising.states.enrollmentstates.EnrolledEnrollmentState;
import edu.advising.states.enrollmentstates.WithdrawnEnrollmentState;
import edu.advising.users.Student;

import java.sql.SQLException;

public class EnrollmentStateMachineTest {
    static void main(){

        // ── Unit: valid transitions ──────────────────────────────────────────
        header("Unit Tests — Valid Transitions");
        test_pendingConfirm_transitionsToEnrolled();
        test_enrolledDrop_transitionsToDropped();
        test_enrolledWithdraw_transitionsToWithdrawn();
        test_enrolledComplete_transitionsToCompleted();
        test_droppedReenroll_transitionsToEnrolled();

        // ── Unit: guard methods ──────────────────────────────────────────────
        header("Unit Tests — Guard Methods");
        test_guards_pendingState();
        test_guards_enrolledState();
        test_guards_droppedState();
        test_guards_withdrawnState();
        test_guards_completedState();

        // ── Unit: illegal transitions ────────────────────────────────────────
        header("Unit Tests — Illegal Transitions");
        test_illegal_dropFromPending();
        test_illegal_confirmFromEnrolled();
        test_illegal_dropFromWithdrawn();
        test_illegal_reenrollFromCompleted();

        // ── Integration tests ────────────────────────────────────────────────
        header("Integration Tests");
        DatabaseManager db = DatabaseManager.getInstance();
        db.seedDatabase();
        integration_register_pendingToEnrolled();
        integration_drop_enrolledToDropped();
        integration_dropWithdrawn_stateUnchanged();
        integration_complete_gradePersistedInDb();

        // ── Summary ──────────────────────────────────────────────────────────
        System.out.println("\n════════════════════════════════");
        System.out.printf("  Results: %d passed, %d failed%n", passed, failed);
        System.out.println("════════════════════════════════");

        db.shutdown();
    }

    // -------------------------------------------------------------------------
    // Helpers
    // -------------------------------------------------------------------------

    /** Builds an in-memory Enrollment in the given status — no DB touch. */
    private static Enrollment makeEnrollment(String status) {
        Enrollment e = new Enrollment();
        e.setStudentId(1);
        e.setSectionId(1);
        e.setStatus(status);
        return e;
    }

    /**
     * Builds a minimal Section with capacity so hasCapacity() returns true.
     * Uses the (sectionNumber, semester, year, capacity) constructor which
     * leaves enrolled=0, so enrolled < capacity is always satisfied.
     */
    private static Section makeSection() {
        return new Section("001", "FALL", 2026, 30);
    }

    /** Reads the enrollment row back from DB and returns its status column. */
    private static String fetchStatusFromDb(int enrollmentId) throws SQLException {
        return DatabaseManager.getInstance().executeQuery(
                "SELECT status FROM enrollments WHERE id = ?",
                rs -> rs.next() ? rs.getString("status") : null,
                enrollmentId
        );
    }

    /** Reads the final_grade column back from DB. */
    private static String fetchFinalGradeFromDb(int enrollmentId) throws SQLException {
        return DatabaseManager.getInstance().executeQuery(
                "SELECT final_grade FROM enrollments WHERE id = ?",
                rs -> rs.next() ? rs.getString("final_grade") : null,
                enrollmentId
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
     * PENDING → ENROLLED via confirm().
     * Expects: state is Enrolled, status string is "ENROLLED".
     */
    static void test_pendingConfirm_transitionsToEnrolled() {
        String name = "PENDING → ENROLLED via confirm()";
        try {
            Enrollment e = makeEnrollment("PENDING");
            Section s    = makeSection();
            // Build a stub student — unit tests don't need a real DB row
            Student student = new Student("u1", "pw", "u@u.com", "A", "B", "S001");
            EnrollmentContext ctx = EnrollmentContext.load(e, student, s);

            ctx.confirm();

            boolean correctState  = ctx.getState() instanceof EnrolledEnrollmentState;
            boolean correctStatus = "ENROLLED".equals(e.getStatus());

            if (correctState && correctStatus) pass(name);
            else fail(name, "state=" + ctx.getState().getClass().getSimpleName()
                    + " status=" + e.getStatus());
        } catch (Exception ex) {
            fail(name, ex.getMessage());
        }
    }

    /**
     * ENROLLED → DROPPED via drop(reason).
     * Expects: state is Dropped, drop_reason is set on the Enrollment.
     */
    static void test_enrolledDrop_transitionsToDropped() {
        String name = "ENROLLED → DROPPED via drop(reason)";
        try {
            Enrollment e = makeEnrollment("ENROLLED");
            Section s    = makeSection();
            Student student = new Student("u1", "pw", "u@u.com", "A", "B", "S001");
            EnrollmentContext ctx = EnrollmentContext.load(e, student, s);

            ctx.drop("PERSONAL");

            boolean correctState  = ctx.getState() instanceof DroppedEnrollmentState;
            boolean correctStatus = "DROPPED".equals(e.getStatus());
            boolean reasonSet     = "PERSONAL".equals(e.getDropReason());

            if (correctState && correctStatus && reasonSet) pass(name);
            else fail(name, "state=" + ctx.getState().getClass().getSimpleName()
                    + " status=" + e.getStatus()
                    + " reason=" + e.getDropReason());
        } catch (Exception ex) {
            fail(name, ex.getMessage());
        }
    }

    /**
     * ENROLLED → WITHDRAWN via withdraw().
     * Expects: state is WithdrawnE, status is "WITHDRAWN".
     */
    static void test_enrolledWithdraw_transitionsToWithdrawn() {
        String name = "ENROLLED → WITHDRAWN via withdraw()";
        try {
            Enrollment e = makeEnrollment("ENROLLED");
            Section s    = makeSection();
            Student student = new Student("u1", "pw", "u@u.com", "A", "B", "S001");
            EnrollmentContext ctx = EnrollmentContext.load(e, student, s);

            ctx.withdraw();

            boolean correctState  = ctx.getState() instanceof WithdrawnEnrollmentState;
            boolean correctStatus = "WITHDRAWN".equals(e.getStatus());

            if (correctState && correctStatus) pass(name);
            else fail(name, "state=" + ctx.getState().getClass().getSimpleName()
                    + " status=" + e.getStatus());
        } catch (Exception ex) {
            fail(name, ex.getMessage());
        }
    }

    /**
     * ENROLLED → COMPLETED via complete(grade).
     * Expects: state is Completed, finalGrade and gradedAt are set.
     */
    static void test_enrolledComplete_transitionsToCompleted() {
        String name = "ENROLLED → COMPLETED via complete(grade)";
        try {
            Enrollment e = makeEnrollment("ENROLLED");
            Section s    = makeSection();
            Student student = new Student("u1", "pw", "u@u.com", "A", "B", "S001");
            EnrollmentContext ctx = EnrollmentContext.load(e, student, s);

            ctx.complete("A");

            boolean correctState  = ctx.getState() instanceof CompletedEnrollmentState;
            boolean correctStatus = "COMPLETED".equals(e.getStatus());
            boolean gradeSet      = "A".equals(e.getFinalGrade());
            boolean gradedAtSet   = e.getGradedAt() != null;

            if (correctState && correctStatus && gradeSet && gradedAtSet) pass(name);
            else fail(name, "state=" + ctx.getState().getClass().getSimpleName()
                    + " status=" + e.getStatus()
                    + " grade=" + e.getFinalGrade()
                    + " gradedAt=" + e.getGradedAt());
        } catch (Exception ex) {
            fail(name, ex.getMessage());
        }
    }

    /**
     * DROPPED → ENROLLED via reenroll() when section has capacity.
     * Expects: state is Enrolled, status is "ENROLLED".
     */
    static void test_droppedReenroll_transitionsToEnrolled() {
        String name = "DROPPED → ENROLLED via reenroll()";
        try {
            Enrollment e = makeEnrollment("DROPPED");
            Section s    = makeSection(); // capacity=30, enrolled=0 → hasCapacity()=true
            Student student = new Student("u1", "pw", "u@u.com", "A", "B", "S001");
            EnrollmentContext ctx = EnrollmentContext.load(e, student, s);

            ctx.reenroll();

            boolean correctState  = ctx.getState() instanceof EnrolledEnrollmentState;
            boolean correctStatus = "ENROLLED".equals(e.getStatus());

            if (correctState && correctStatus) pass(name);
            else fail(name, "state=" + ctx.getState().getClass().getSimpleName()
                    + " status=" + e.getStatus());
        } catch (Exception ex) {
            fail(name, ex.getMessage());
        }
    }

    // -------------------------------------------------------------------------
    // Unit Tests — Guard Methods
    // -------------------------------------------------------------------------

    static void test_guards_pendingState() {
        String name = "Guards: PENDING state";
        try {
            Enrollment e = makeEnrollment("PENDING");
            Student student = new Student("u1", "pw", "u@u.com", "A", "B", "S001");
            EnrollmentContext ctx = EnrollmentContext.load(e, student, makeSection());

            boolean ok = !ctx.canDrop()
                    && !ctx.canWithdraw()
                    && !ctx.canComplete()
                    && !ctx.canReenroll();

            if (ok) pass(name);
            else fail(name, "canDrop=" + ctx.canDrop()
                    + " canWithdraw=" + ctx.canWithdraw()
                    + " canComplete=" + ctx.canComplete()
                    + " canReenroll=" + ctx.canReenroll());
        } catch (Exception ex) {
            fail(name, ex.getMessage());
        }
    }

    static void test_guards_enrolledState() {
        String name = "Guards: ENROLLED state";
        try {
            Enrollment e = makeEnrollment("ENROLLED");
            Student student = new Student("u1", "pw", "u@u.com", "A", "B", "S001");
            EnrollmentContext ctx = EnrollmentContext.load(e, student, makeSection());

            boolean ok = ctx.canDrop()
                    && ctx.canWithdraw()
                    && ctx.canComplete()
                    && !ctx.canReenroll();

            if (ok) pass(name);
            else fail(name, "canDrop=" + ctx.canDrop()
                    + " canWithdraw=" + ctx.canWithdraw()
                    + " canComplete=" + ctx.canComplete()
                    + " canReenroll=" + ctx.canReenroll());
        } catch (Exception ex) {
            fail(name, ex.getMessage());
        }
    }

    static void test_guards_droppedState() {
        String name = "Guards: DROPPED state";
        try {
            Enrollment e = makeEnrollment("DROPPED");
            Student student = new Student("u1", "pw", "u@u.com", "A", "B", "S001");
            EnrollmentContext ctx = EnrollmentContext.load(e, student, makeSection());

            boolean ok = !ctx.canDrop()
                    && !ctx.canWithdraw()
                    && !ctx.canComplete()
                    && ctx.canReenroll();

            if (ok) pass(name);
            else fail(name, "canDrop=" + ctx.canDrop()
                    + " canWithdraw=" + ctx.canWithdraw()
                    + " canComplete=" + ctx.canComplete()
                    + " canReenroll=" + ctx.canReenroll());
        } catch (Exception ex) {
            fail(name, ex.getMessage());
        }
    }

    static void test_guards_withdrawnState() {
        String name = "Guards: WITHDRAWN state — all false";
        try {
            Enrollment e = makeEnrollment("WITHDRAWN");
            Student student = new Student("u1", "pw", "u@u.com", "A", "B", "S001");
            EnrollmentContext ctx = EnrollmentContext.load(e, student, makeSection());

            boolean ok = !ctx.canDrop()
                    && !ctx.canWithdraw()
                    && !ctx.canComplete()
                    && !ctx.canReenroll();

            if (ok) pass(name);
            else fail(name, "canDrop=" + ctx.canDrop()
                    + " canWithdraw=" + ctx.canWithdraw()
                    + " canComplete=" + ctx.canComplete()
                    + " canReenroll=" + ctx.canReenroll());
        } catch (Exception ex) {
            fail(name, ex.getMessage());
        }
    }

    static void test_guards_completedState() {
        String name = "Guards: COMPLETED state — all false";
        try {
            Enrollment e = makeEnrollment("COMPLETED");
            Student student = new Student("u1", "pw", "u@u.com", "A", "B", "S001");
            EnrollmentContext ctx = EnrollmentContext.load(e, student, makeSection());

            boolean ok = !ctx.canDrop()
                    && !ctx.canWithdraw()
                    && !ctx.canComplete()
                    && !ctx.canReenroll();

            if (ok) pass(name);
            else fail(name, "canDrop=" + ctx.canDrop()
                    + " canWithdraw=" + ctx.canWithdraw()
                    + " canComplete=" + ctx.canComplete()
                    + " canReenroll=" + ctx.canReenroll());
        } catch (Exception ex) {
            fail(name, ex.getMessage());
        }
    }

    // -------------------------------------------------------------------------
    // Unit Tests — Illegal Transitions
    // -------------------------------------------------------------------------

    /**
     * Illegal: drop() from PENDING — expects IllegalStateException,
     * state must not change.
     */
    static void test_illegal_dropFromPending() {
        String name = "Illegal: drop() from PENDING does not change state";
        try {
            Enrollment e = makeEnrollment("PENDING");
            Student student = new Student("u1", "pw", "u@u.com", "A", "B", "S001");
            EnrollmentContext ctx = EnrollmentContext.load(e, student, makeSection());

            try {
                ctx.drop("REASON");
                fail(name, "Expected IllegalStateException but none was thrown");
                return;
            } catch (IllegalStateException expected) {
                // correct — now verify state is unchanged
            }

            boolean stateUnchanged  = ctx.getState() instanceof
                    edu.advising.states.enrollmentstates.PendingEnrollmentState;
            boolean statusUnchanged = "PENDING".equals(e.getStatus());

            if (stateUnchanged && statusUnchanged) pass(name);
            else fail(name, "State or status changed after illegal transition: "
                    + ctx.getState().getClass().getSimpleName() + " / " + e.getStatus());
        } catch (Exception ex) {
            fail(name, ex.getMessage());
        }
    }

    /**
     * Illegal: confirm() from ENROLLED — expects IllegalStateException,
     * state must not change.
     */
    static void test_illegal_confirmFromEnrolled() {
        String name = "Illegal: confirm() from ENROLLED does not change state";
        try {
            Enrollment e = makeEnrollment("ENROLLED");
            Student student = new Student("u1", "pw", "u@u.com", "A", "B", "S001");
            EnrollmentContext ctx = EnrollmentContext.load(e, student, makeSection());

            try {
                ctx.confirm();
                fail(name, "Expected IllegalStateException but none was thrown");
                return;
            } catch (IllegalStateException expected) {}

            boolean stateUnchanged  = ctx.getState() instanceof EnrolledEnrollmentState;
            boolean statusUnchanged = "ENROLLED".equals(e.getStatus());

            if (stateUnchanged && statusUnchanged) pass(name);
            else fail(name, "State changed: " + ctx.getState().getClass().getSimpleName());
        } catch (Exception ex) {
            fail(name, ex.getMessage());
        }
    }

    /**
     * Illegal: drop() from WITHDRAWN — terminal state, must not change.
     */
    static void test_illegal_dropFromWithdrawn() {
        String name = "Illegal: drop() from WITHDRAWN does not change state";
        try {
            Enrollment e = makeEnrollment("WITHDRAWN");
            Student student = new Student("u1", "pw", "u@u.com", "A", "B", "S001");
            EnrollmentContext ctx = EnrollmentContext.load(e, student, makeSection());

            try {
                ctx.drop("REASON");
                fail(name, "Expected IllegalStateException but none was thrown");
                return;
            } catch (IllegalStateException expected) {}

            boolean stateUnchanged  = ctx.getState() instanceof WithdrawnEnrollmentState;
            boolean statusUnchanged = "WITHDRAWN".equals(e.getStatus());

            if (stateUnchanged && statusUnchanged) pass(name);
            else fail(name, "State changed: " + ctx.getState().getClass().getSimpleName());
        } catch (Exception ex) {
            fail(name, ex.getMessage());
        }
    }

    /**
     * Illegal: reenroll() from COMPLETED — terminal state, must not change.
     */
    static void test_illegal_reenrollFromCompleted() {
        String name = "Illegal: reenroll() from COMPLETED does not change state";
        try {
            Enrollment e = makeEnrollment("COMPLETED");
            Student student = new Student("u1", "pw", "u@u.com", "A", "B", "S001");
            EnrollmentContext ctx = EnrollmentContext.load(e, student, makeSection());

            try {
                ctx.reenroll();
                fail(name, "Expected IllegalStateException but none was thrown");
                return;
            } catch (IllegalStateException expected) {}

            boolean stateUnchanged  = ctx.getState() instanceof CompletedEnrollmentState;
            boolean statusUnchanged = "COMPLETED".equals(e.getStatus());

            if (stateUnchanged && statusUnchanged) pass(name);
            else fail(name, "State changed: " + ctx.getState().getClass().getSimpleName());
        } catch (Exception ex) {
            fail(name, ex.getMessage());
        }
    }

    // -------------------------------------------------------------------------
    // Integration Tests
    // -------------------------------------------------------------------------

    /**
     * Integration: EnrollmentContext.create() persists PENDING, then confirm()
     * updates DB row to ENROLLED.
     */
    static void integration_register_pendingToEnrolled() {
        String name = "Integration: register → PENDING → ENROLLED in DB";
        try {
            DatabaseManager db = DatabaseManager.getInstance();

            // Seed minimal data: one student, one section
            int userId = db.executeInsert(
                    "INSERT INTO users (username, password, user_type, first_name, last_name, email) " +
                            "VALUES ('it_student', 'pw', 'STUDENT', 'Int', 'Test', 'it@test.com')");
            db.executeInsert(
                    "INSERT INTO students (id, student_id) VALUES (?, 'IT001')", userId);

            int deptId = db.executeInsert(
                    "INSERT INTO departments (code, name) VALUES ('IT', 'Integration Test Dept')");
            int courseId = db.executeInsert(
                    "INSERT INTO courses (code, name, description, credits, department_id, level) " +
                            "VALUES ('IT101', 'Test Course', 'desc', 3, ?, '100')", deptId);
            int sectionId = db.executeInsert(
                    "INSERT INTO sections (course_id, section_number, semester, `year`, capacity, enrolled, status) " +
                            "VALUES (?, '001', 'FALL', 2026, 30, 0, 'OPEN')", courseId);

            // Build domain objects from DB IDs
            Student student = DatabaseManager.getInstance()
                    .fetchOne(Student.class, "id", userId);
            Section section = DatabaseManager.getInstance()
                    .fetchOne(Section.class, "id", sectionId);

            // ACT
            EnrollmentContext ctx = EnrollmentContext.create(student, section);
            int enrollmentId = ctx.getEnrollment().getId();

            // Verify PENDING was persisted before confirm()
            String statusAfterCreate = fetchStatusFromDb(enrollmentId);
            if (!"PENDING".equals(statusAfterCreate)) {
                fail(name, "Expected PENDING after create(), got: " + statusAfterCreate);
                return;
            }

            ctx.confirm();

            String statusAfterConfirm = fetchStatusFromDb(enrollmentId);
            if ("ENROLLED".equals(statusAfterConfirm)) pass(name);
            else fail(name, "Expected ENROLLED after confirm(), got: " + statusAfterConfirm);

        } catch (Exception ex) {
            fail(name, ex.toString());
        }
    }

    /**
     * Integration: load an ENROLLED enrollment, call drop(), verify DB shows DROPPED.
     */
    static void integration_drop_enrolledToDropped() {
        String name = "Integration: drop() → ENROLLED → DROPPED in DB";
        try {
            DatabaseManager db = DatabaseManager.getInstance();

            int userId = db.executeInsert(
                    "INSERT INTO users (username, password, user_type, first_name, last_name, email) " +
                            "VALUES ('drop_student', 'pw', 'STUDENT', 'Drop', 'Test', 'drop@test.com')");
            db.executeInsert(
                    "INSERT INTO students (id, student_id) VALUES (?, 'DR001')", userId);

            int deptId = db.executeInsert(
                    "INSERT INTO departments (code, name) VALUES ('DR', 'Drop Test Dept')");
            int courseId = db.executeInsert(
                    "INSERT INTO courses (code, name, description, credits, department_id, level) " +
                            "VALUES ('DR101', 'Drop Course', 'desc', 3, ?, '100')", deptId);
            int sectionId = db.executeInsert(
                    "INSERT INTO sections (course_id, section_number, semester, `year`, capacity, enrolled, status) " +
                            "VALUES (?, '001', 'FALL', 2026, 30, 1, 'OPEN')", courseId);

            // Insert an already-ENROLLED row directly
            int enrollmentId = db.executeInsert(
                    "INSERT INTO enrollments (student_id, section_id, status) VALUES (?, ?, 'ENROLLED')",
                    userId, sectionId);

            Student student = DatabaseManager.getInstance().fetchOne(Student.class, "id", userId);
            Section section = DatabaseManager.getInstance().fetchOne(Section.class, "id", sectionId);

            // Fetch the enrollment row
            Enrollment enrollment = db.fetch(
                    "SELECT * FROM enrollments WHERE id = ?",
                    rs -> {
                        Enrollment e = new Enrollment();
                        e.setId(rs.getInt("id"));
                        e.setStudentId(rs.getInt("student_id"));
                        e.setSectionId(rs.getInt("section_id"));
                        e.setStatus(rs.getString("status"));
                        return e;
                    },
                    enrollmentId);

            // ACT
            EnrollmentContext ctx = EnrollmentContext.load(enrollment, student, section);
            ctx.drop("PERSONAL");

            String statusAfterDrop = fetchStatusFromDb(enrollmentId);
            if ("DROPPED".equals(statusAfterDrop)) pass(name);
            else fail(name, "Expected DROPPED, got: " + statusAfterDrop);

        } catch (Exception ex) {
            fail(name, ex.toString());
        }
    }

    /**
     * Integration: attempt drop() on a WITHDRAWN enrollment — DB row must be unchanged.
     */
    static void integration_dropWithdrawn_stateUnchanged() {
        String name = "Integration: drop() on WITHDRAWN — DB row unchanged";
        try {
            DatabaseManager db = DatabaseManager.getInstance();

            int userId = db.executeInsert(
                    "INSERT INTO users (username, password, user_type, first_name, last_name, email) " +
                            "VALUES ('wd_student', 'pw', 'STUDENT', 'WD', 'Test', 'wd@test.com')");
            db.executeInsert(
                    "INSERT INTO students (id, student_id) VALUES (?, 'WD001')", userId);

            int deptId = db.executeInsert(
                    "INSERT INTO departments (code, name) VALUES ('WD', 'Withdrawn Test Dept')");
            int courseId = db.executeInsert(
                    "INSERT INTO courses (code, name, description, credits, department_id, level) " +
                            "VALUES ('WD101', 'WD Course', 'desc', 3, ?, '100')", deptId);
            int sectionId = db.executeInsert(
                    "INSERT INTO sections (course_id, section_number, semester, `year`, capacity, enrolled, status) " +
                            "VALUES (?, '001', 'FALL', 2026, 30, 0, 'OPEN')", courseId);

            int enrollmentId = db.executeInsert(
                    "INSERT INTO enrollments (student_id, section_id, status) VALUES (?, ?, 'WITHDRAWN')",
                    userId, sectionId);

            Student student = DatabaseManager.getInstance().fetchOne(Student.class, "id", userId);
            Section section = DatabaseManager.getInstance().fetchOne(Section.class, "id", sectionId);

            Enrollment enrollment = db.fetch(
                    "SELECT * FROM enrollments WHERE id = ?",
                    rs -> {
                        Enrollment e = new Enrollment();
                        e.setId(rs.getInt("id"));
                        e.setStudentId(rs.getInt("student_id"));
                        e.setSectionId(rs.getInt("section_id"));
                        e.setStatus(rs.getString("status"));
                        return e;
                    },
                    enrollmentId);

            // ACT — should throw, state must not persist
            EnrollmentContext ctx = EnrollmentContext.load(enrollment, student, section);
            try {
                ctx.drop("REASON");
            } catch (IllegalStateException expected) {
                // correct — now verify DB is still WITHDRAWN
            }

            String statusInDb = fetchStatusFromDb(enrollmentId);
            if ("WITHDRAWN".equals(statusInDb)) pass(name);
            else fail(name, "DB row changed — expected WITHDRAWN, got: " + statusInDb);

        } catch (Exception ex) {
            fail(name, ex.toString());
        }
    }

    /**
     * Integration: complete() writes COMPLETED status and final_grade to DB.
     */
    static void integration_complete_gradePersistedInDb() {
        String name = "Integration: complete() → COMPLETED and grade in DB";
        try {
            DatabaseManager db = DatabaseManager.getInstance();

            int userId = db.executeInsert(
                    "INSERT INTO users (username, password, user_type, first_name, last_name, email) " +
                            "VALUES ('cmp_student', 'pw', 'STUDENT', 'Cmp', 'Test', 'cmp@test.com')");
            db.executeInsert(
                    "INSERT INTO students (id, student_id) VALUES (?, 'CM001')", userId);

            int deptId = db.executeInsert(
                    "INSERT INTO departments (code, name) VALUES ('CM', 'Complete Test Dept')");
            int courseId = db.executeInsert(
                    "INSERT INTO courses (code, name, description, credits, department_id, level) " +
                            "VALUES ('CM101', 'Complete Course', 'desc', 3, ?, '100')", deptId);
            int sectionId = db.executeInsert(
                    "INSERT INTO sections (course_id, section_number, semester, `year`, capacity, enrolled, status) " +
                            "VALUES (?, '001', 'FALL', 2026, 30, 1, 'OPEN')", courseId);

            int enrollmentId = db.executeInsert(
                    "INSERT INTO enrollments (student_id, section_id, status) VALUES (?, ?, 'ENROLLED')",
                    userId, sectionId);

            Student student = DatabaseManager.getInstance().fetchOne(Student.class, "id", userId);
            Section section = DatabaseManager.getInstance().fetchOne(Section.class, "id", sectionId);

            Enrollment enrollment = db.fetch(
                    "SELECT * FROM enrollments WHERE id = ?",
                    rs -> {
                        Enrollment e = new Enrollment();
                        e.setId(rs.getInt("id"));
                        e.setStudentId(rs.getInt("student_id"));
                        e.setSectionId(rs.getInt("section_id"));
                        e.setStatus(rs.getString("status"));
                        return e;
                    },
                    enrollmentId);

            // ACT
            EnrollmentContext ctx = EnrollmentContext.load(enrollment, student, section);
            ctx.complete("B+");

            String statusInDb = fetchStatusFromDb(enrollmentId);
            String gradeInDb  = fetchFinalGradeFromDb(enrollmentId);

            boolean statusOk = "COMPLETED".equals(statusInDb);
            boolean gradeOk  = "B+".equals(gradeInDb);

            if (statusOk && gradeOk) pass(name);
            else fail(name, "status=" + statusInDb + " grade=" + gradeInDb);

        } catch (Exception ex) {
            fail(name, ex.toString());
        }
    }

}
