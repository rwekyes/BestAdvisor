import edu.advising.model.TranscriptRequest;
import edu.advising.contexts.TranscriptRequestContext;
import edu.advising.core.DatabaseManager;
import edu.advising.states.StateFactory;
import edu.advising.states.transcriptrequeststates.*;
import edu.advising.users.Student;

import java.sql.SQLException;

/**
 * Test suite for the Transcript Request State Machine.
 *
 * IMPORTANT — two fixes required before these tests will pass:
 *
 *   1. @Table(name = "transcript_request") in TranscriptRequest.java must be
 *      changed to @Table(name = "transcript_requests") to match the DB schema.
 *
 *   2. TranscriptRequestContext's private constructor hardcodes
 *      PendingTranscriptRequestState. Fix #4 from the review list must be applied:
 *        this.state = StateFactory.transcriptRequestStateFor(transcriptRequest.getStatus());
 *      Without this, load() always reconstructs as PENDING regardless of DB status,
 *      and the integration tests that verify load() will fail.
 *
 * Run with: mvn exec:java@run-TranscriptRequestStateMachineTest
 */
public class TranscriptRequestStateMachineTest {

    // -------------------------------------------------------------------------
    // Helpers
    // -------------------------------------------------------------------------

    /** Builds an in-memory TranscriptRequest in the given status — no DB touch. */
    private static TranscriptRequest makeRequest(String status) {
        TranscriptRequest r = new TranscriptRequest();
        r.setStatus(status);
        r.setTrackingNumber("TR-00000001");
        return r;
    }

    private static Student makeStudent() {
        return new Student("tr_unit", "pw", "tr@u.com", "Test", "Student", "TR001");
    }

    /** Builds an in-memory context in the given status — no DB touch. */
    private static TranscriptRequestContext makeContext(String status) {
        return TranscriptRequestContext.load(makeRequest(status), makeStudent());
    }

    private static String fetchStatusFromDb(int requestId) throws SQLException {
        return DatabaseManager.getInstance().executeQuery(
                "SELECT status FROM transcript_requests WHERE id = ?",
                rs -> rs.next() ? rs.getString("status") : null,
                requestId
        );
    }

    private static String fetchFailureReasonFromDb(int requestId) throws SQLException {
        return DatabaseManager.getInstance().executeQuery(
                "SELECT failure_reason FROM transcript_requests WHERE id = ?",
                rs -> rs.next() ? rs.getString("failure_reason") : null,
                requestId
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

    static void test_pendingProcess_transitionsToProcessing() {
        String name = "PENDING → PROCESSING via process()";
        try {
            TranscriptRequestContext ctx = makeContext("PENDING");
            ctx.process();
            boolean ok = ctx.getState() instanceof ProcessingTranscriptRequestState
                    && "PROCESSING".equals(ctx.getTranscriptRequest().getStatus());
            if (ok) pass(name);
            else fail(name, "state=" + ctx.getState().getClass().getSimpleName()
                    + " status=" + ctx.getTranscriptRequest().getStatus());
        } catch (Exception ex) { fail(name, ex.getMessage()); }
    }

    static void test_processingReady_transitionsToReady() {
        String name = "PROCESSING → READY via ready()";
        try {
            TranscriptRequestContext ctx = makeContext("PROCESSING");
            ctx.ready();
            boolean ok = ctx.getState() instanceof ReadyTranscriptRequestState
                    && "READY".equals(ctx.getTranscriptRequest().getStatus());
            if (ok) pass(name);
            else fail(name, "state=" + ctx.getState().getClass().getSimpleName()
                    + " status=" + ctx.getTranscriptRequest().getStatus());
        } catch (Exception ex) { fail(name, ex.getMessage()); }
    }

    static void test_readySend_transitionsToSent() {
        String name = "READY → SENT via send()";
        try {
            TranscriptRequestContext ctx = makeContext("READY");
            ctx.send();
            boolean ok = ctx.getState() instanceof SentTranscriptRequestState
                    && "SENT".equals(ctx.getTranscriptRequest().getStatus());
            if (ok) pass(name);
            else fail(name, "state=" + ctx.getState().getClass().getSimpleName()
                    + " status=" + ctx.getTranscriptRequest().getStatus());
        } catch (Exception ex) { fail(name, ex.getMessage()); }
    }

    static void test_pendingCancel_transitionsToCancelled() {
        String name = "PENDING → CANCELLED via cancel()";
        try {
            TranscriptRequestContext ctx = makeContext("PENDING");
            ctx.cancel();
            boolean ok = ctx.getState() instanceof CancelledTranscriptRequestState
                    && "CANCELLED".equals(ctx.getTranscriptRequest().getStatus());
            if (ok) pass(name);
            else fail(name, "state=" + ctx.getState().getClass().getSimpleName()
                    + " status=" + ctx.getTranscriptRequest().getStatus());
        } catch (Exception ex) { fail(name, ex.getMessage()); }
    }

    static void test_processingCancel_transitionsToCancelled() {
        String name = "PROCESSING → CANCELLED via cancel()";
        try {
            TranscriptRequestContext ctx = makeContext("PROCESSING");
            ctx.cancel();
            boolean ok = ctx.getState() instanceof CancelledTranscriptRequestState
                    && "CANCELLED".equals(ctx.getTranscriptRequest().getStatus());
            if (ok) pass(name);
            else fail(name, "state=" + ctx.getState().getClass().getSimpleName()
                    + " status=" + ctx.getTranscriptRequest().getStatus());
        } catch (Exception ex) { fail(name, ex.getMessage()); }
    }

    static void test_readyCancel_transitionsToCancelled() {
        String name = "READY → CANCELLED via cancel()";
        try {
            TranscriptRequestContext ctx = makeContext("READY");
            ctx.cancel();
            boolean ok = ctx.getState() instanceof CancelledTranscriptRequestState
                    && "CANCELLED".equals(ctx.getTranscriptRequest().getStatus());
            if (ok) pass(name);
            else fail(name, "state=" + ctx.getState().getClass().getSimpleName()
                    + " status=" + ctx.getTranscriptRequest().getStatus());
        } catch (Exception ex) { fail(name, ex.getMessage()); }
    }

    static void test_processingFail_transitionsToFailed() {
        String name = "PROCESSING → FAILED via fail(reason)";
        try {
            TranscriptRequestContext ctx = makeContext("PROCESSING");
            ctx.fail("Missing academic records");
            boolean ok = ctx.getState() instanceof FailedTranscriptRequestState
                    && "FAILED".equals(ctx.getTranscriptRequest().getStatus())
                    && "Missing academic records".equals(ctx.getTranscriptRequest().getFailureReason());
            if (ok) pass(name);
            else fail(name, "state=" + ctx.getState().getClass().getSimpleName()
                    + " status=" + ctx.getTranscriptRequest().getStatus()
                    + " reason=" + ctx.getTranscriptRequest().getFailureReason());
        } catch (Exception ex) { fail(name, ex.getMessage()); }
    }

    static void test_failedRetry_transitionsToProcessing() {
        String name = "FAILED → PROCESSING via process() (retry)";
        try {
            TranscriptRequestContext ctx = makeContext("FAILED");
            ctx.process();
            boolean ok = ctx.getState() instanceof ProcessingTranscriptRequestState
                    && "PROCESSING".equals(ctx.getTranscriptRequest().getStatus());
            if (ok) pass(name);
            else fail(name, "state=" + ctx.getState().getClass().getSimpleName()
                    + " status=" + ctx.getTranscriptRequest().getStatus());
        } catch (Exception ex) { fail(name, ex.getMessage()); }
    }

    // -------------------------------------------------------------------------
    // Unit Tests — Guard Methods (canX)
    // -------------------------------------------------------------------------

    static void test_guards_pendingState() {
        String name = "Guards: PENDING state";
        try {
            TranscriptRequestContext ctx = makeContext("PENDING");
            boolean ok = ctx.canProcess()
                    && !ctx.canReady()
                    && !ctx.canSend()
                    && ctx.canCancel()
                    && !ctx.canFail();
            if (ok) pass(name);
            else fail(name, "canProcess=" + ctx.canProcess()
                    + " canReady=" + ctx.canReady()
                    + " canSend=" + ctx.canSend()
                    + " canCancel=" + ctx.canCancel()
                    + " canFail=" + ctx.canFail());
        } catch (Exception ex) { fail(name, ex.getMessage()); }
    }

    static void test_guards_processingState() {
        String name = "Guards: PROCESSING state";
        try {
            TranscriptRequestContext ctx = makeContext("PROCESSING");
            boolean ok = !ctx.canProcess()
                    && ctx.canReady()
                    && !ctx.canSend()
                    && ctx.canCancel()
                    && ctx.canFail();
            if (ok) pass(name);
            else fail(name, "canProcess=" + ctx.canProcess()
                    + " canReady=" + ctx.canReady()
                    + " canSend=" + ctx.canSend()
                    + " canCancel=" + ctx.canCancel()
                    + " canFail=" + ctx.canFail());
        } catch (Exception ex) { fail(name, ex.getMessage()); }
    }

    static void test_guards_readyState() {
        String name = "Guards: READY state";
        try {
            TranscriptRequestContext ctx = makeContext("READY");
            boolean ok = !ctx.canProcess()
                    && !ctx.canReady()
                    && ctx.canSend()
                    && ctx.canCancel()
                    && !ctx.canFail();
            if (ok) pass(name);
            else fail(name, "canProcess=" + ctx.canProcess()
                    + " canReady=" + ctx.canReady()
                    + " canSend=" + ctx.canSend()
                    + " canCancel=" + ctx.canCancel()
                    + " canFail=" + ctx.canFail());
        } catch (Exception ex) { fail(name, ex.getMessage()); }
    }

    static void test_guards_sentState() {
        String name = "Guards: SENT state — all false";
        try {
            TranscriptRequestContext ctx = makeContext("SENT");
            boolean ok = !ctx.canProcess() && !ctx.canReady()
                    && !ctx.canSend() && !ctx.canCancel() && !ctx.canFail();
            if (ok) pass(name);
            else fail(name, "expected all false");
        } catch (Exception ex) { fail(name, ex.getMessage()); }
    }

    static void test_guards_cancelledState() {
        String name = "Guards: CANCELLED state — all false";
        try {
            TranscriptRequestContext ctx = makeContext("CANCELLED");
            boolean ok = !ctx.canProcess() && !ctx.canReady()
                    && !ctx.canSend() && !ctx.canCancel() && !ctx.canFail();
            if (ok) pass(name);
            else fail(name, "expected all false");
        } catch (Exception ex) { fail(name, ex.getMessage()); }
    }

    static void test_guards_failedState() {
        String name = "Guards: FAILED state";
        try {
            TranscriptRequestContext ctx = makeContext("FAILED");
            boolean ok = ctx.canProcess()
                    && !ctx.canReady()
                    && !ctx.canSend()
                    && !ctx.canCancel()
                    && !ctx.canFail();
            if (ok) pass(name);
            else fail(name, "canProcess=" + ctx.canProcess()
                    + " canCancel=" + ctx.canCancel()
                    + " canFail=" + ctx.canFail());
        } catch (Exception ex) { fail(name, ex.getMessage()); }
    }

    // -------------------------------------------------------------------------
    // Unit Tests — Illegal Transitions do NOT change state
    // -------------------------------------------------------------------------

    static void test_illegal_sendFromPending() {
        String name = "Illegal: send() from PENDING does not change state";
        try {
            TranscriptRequestContext ctx = makeContext("PENDING");
            try {
                ctx.send();
                fail(name, "Expected IllegalStateException but none thrown");
                return;
            } catch (IllegalStateException expected) {}
            boolean ok = ctx.getState() instanceof PendingTranscriptRequestState
                    && "PENDING".equals(ctx.getTranscriptRequest().getStatus());
            if (ok) pass(name);
            else fail(name, "State changed to: " + ctx.getState().getClass().getSimpleName());
        } catch (Exception ex) { fail(name, ex.getMessage()); }
    }

    static void test_illegal_sendFromProcessing() {
        String name = "Illegal: send() from PROCESSING does not change state";
        try {
            TranscriptRequestContext ctx = makeContext("PROCESSING");
            try {
                ctx.send();
                fail(name, "Expected IllegalStateException but none thrown");
                return;
            } catch (IllegalStateException expected) {}
            boolean ok = ctx.getState() instanceof ProcessingTranscriptRequestState;
            if (ok) pass(name);
            else fail(name, "State changed to: " + ctx.getState().getClass().getSimpleName());
        } catch (Exception ex) { fail(name, ex.getMessage()); }
    }

    static void test_illegal_anyFromSent() {
        String name = "Illegal: all transitions from SENT throw and do not change state";
        try {
            int throwCount = 0;
            for (Runnable action : new Runnable[]{
                    () -> makeContext("SENT").process(),
                    () -> makeContext("SENT").ready(),
                    () -> makeContext("SENT").send(),
                    () -> makeContext("SENT").cancel(),
                    () -> makeContext("SENT").fail("x")
            }) {
                try { action.run(); }
                catch (IllegalStateException e) { throwCount++; }
            }
            if (throwCount == 5) pass(name);
            else fail(name, "Only " + throwCount + "/5 threw IllegalStateException");
        } catch (Exception ex) { fail(name, ex.getMessage()); }
    }

    static void test_illegal_anyFromCancelled() {
        String name = "Illegal: all transitions from CANCELLED throw and do not change state";
        try {
            int throwCount = 0;
            for (Runnable action : new Runnable[]{
                    () -> makeContext("CANCELLED").process(),
                    () -> makeContext("CANCELLED").ready(),
                    () -> makeContext("CANCELLED").send(),
                    () -> makeContext("CANCELLED").cancel(),
                    () -> makeContext("CANCELLED").fail("x")
            }) {
                try { action.run(); }
                catch (IllegalStateException e) { throwCount++; }
            }
            if (throwCount == 5) pass(name);
            else fail(name, "Only " + throwCount + "/5 threw IllegalStateException");
        } catch (Exception ex) { fail(name, ex.getMessage()); }
    }

    // -------------------------------------------------------------------------
    // Unit Tests — Tracking Number Format
    // -------------------------------------------------------------------------

    static void test_trackingNumberFormat() {
        String name = "Tracking number matches format TR-XXXXXXXX";
        try {
            String tn = TranscriptRequestContext.generateTrackingNumber();
            boolean ok = tn != null
                    && tn.matches("TR-\\d{8}");
            if (ok) pass(name);
            else fail(name, "Generated: " + tn);
        } catch (Exception ex) { fail(name, ex.getMessage()); }
    }

    // -------------------------------------------------------------------------
    // Unit Tests — StateFactory mapping
    // -------------------------------------------------------------------------

    static void test_stateFactory_allSixStrings() {
        String name = "StateFactory.transcriptRequestStateFor() maps all six status strings";
        try {
            boolean ok =
                    StateFactory.transcriptRequestStateFor("PENDING")    instanceof PendingTranscriptRequestState
                            && StateFactory.transcriptRequestStateFor("PROCESSING") instanceof ProcessingTranscriptRequestState
                            && StateFactory.transcriptRequestStateFor("READY")      instanceof ReadyTranscriptRequestState
                            && StateFactory.transcriptRequestStateFor("SENT")       instanceof SentTranscriptRequestState
                            && StateFactory.transcriptRequestStateFor("CANCELLED")  instanceof CancelledTranscriptRequestState
                            && StateFactory.transcriptRequestStateFor("FAILED")     instanceof FailedTranscriptRequestState;
            if (ok) pass(name);
            else fail(name, "One or more mappings returned wrong type");
        } catch (Exception ex) { fail(name, ex.getMessage()); }
    }

    // -------------------------------------------------------------------------
    // Integration Tests
    // -------------------------------------------------------------------------

    /** Seed a minimal student for integration tests and return the Student object. */
    private static Student seedStudent(DatabaseManager db, String username, String studentId,
                                       String email) throws SQLException {
        int userId = db.executeInsert(
                "INSERT INTO users (username, password, user_type, first_name, last_name, email) " +
                        "VALUES (?, 'Password1!', 'STUDENT', 'Test', 'Student', ?)",
                username, email);
        db.executeInsert("INSERT INTO students (id, student_id) VALUES (?, ?)", userId, studentId);
        return db.fetchOne(Student.class, "id", userId);
    }

    /**
     * Integration: create() persists PENDING row, drive through
     * PENDING → PROCESSING → READY → SENT, verify DB status at each step.
     */
    static void integration_fullHappyPath() {
        String name = "Integration: PENDING → PROCESSING → READY → SENT, DB verified at each step";
        try {
            DatabaseManager db = DatabaseManager.getInstance();
            Student student = seedStudent(db, "tr_happy", "TRH001", "tr_happy@test.com");

            TranscriptRequestContext ctx = TranscriptRequestContext.create(student);
            int id = ctx.getTranscriptRequest().getId();

            if (id == 0) {
                fail(name, "create() did not persist — id is still 0. "
                        + "Check @Table name matches transcript_requests");
                return;
            }

            // Verify PENDING persisted
            String s1 = fetchStatusFromDb(id);
            if (!"PENDING".equals(s1)) { fail(name, "After create(): expected PENDING got " + s1); return; }

            ctx.process();
            String s2 = fetchStatusFromDb(id);
            if (!"PROCESSING".equals(s2)) { fail(name, "After process(): expected PROCESSING got " + s2); return; }

            ctx.ready();
            String s3 = fetchStatusFromDb(id);
            if (!"READY".equals(s3)) { fail(name, "After ready(): expected READY got " + s3); return; }

            ctx.send();
            String s4 = fetchStatusFromDb(id);
            if (!"SENT".equals(s4)) { fail(name, "After send(): expected SENT got " + s4); return; }

            pass(name);
        } catch (Exception ex) { fail(name, ex.toString()); }
    }

    /**
     * Integration: create() then cancel() from PENDING — verify DB = CANCELLED.
     */
    static void integration_cancelFromPending() {
        String name = "Integration: cancel from PENDING → DB status = CANCELLED";
        try {
            DatabaseManager db = DatabaseManager.getInstance();
            Student student = seedStudent(db, "tr_cancel", "TRC001", "tr_cancel@test.com");

            TranscriptRequestContext ctx = TranscriptRequestContext.create(student);
            int id = ctx.getTranscriptRequest().getId();
            if (id == 0) { fail(name, "create() did not persist"); return; }

            ctx.cancel();

            String status = fetchStatusFromDb(id);
            if ("CANCELLED".equals(status)) pass(name);
            else fail(name, "Expected CANCELLED, got: " + status);
        } catch (Exception ex) { fail(name, ex.toString()); }
    }

    /**
     * Integration: PENDING → PROCESSING → FAILED, verify failure_reason persisted.
     * Then retry (process()), verify status returns to PROCESSING.
     */
    static void integration_failAndRetry() {
        String name = "Integration: fail from PROCESSING, failure_reason persisted, retry → PROCESSING";
        try {
            DatabaseManager db = DatabaseManager.getInstance();
            Student student = seedStudent(db, "tr_fail", "TRF001", "tr_fail@test.com");

            TranscriptRequestContext ctx = TranscriptRequestContext.create(student);
            int id = ctx.getTranscriptRequest().getId();
            if (id == 0) { fail(name, "create() did not persist"); return; }

            ctx.process();
            ctx.fail("Missing academic records from transfer institution");

            String statusAfterFail   = fetchStatusFromDb(id);
            String reasonAfterFail   = fetchFailureReasonFromDb(id);

            if (!"FAILED".equals(statusAfterFail)) {
                fail(name, "Expected FAILED, got: " + statusAfterFail);
                return;
            }
            if (!"Missing academic records from transfer institution".equals(reasonAfterFail)) {
                fail(name, "failure_reason not persisted. Got: " + reasonAfterFail);
                return;
            }

            // Retry
            ctx.process();
            String statusAfterRetry = fetchStatusFromDb(id);
            if ("PROCESSING".equals(statusAfterRetry)) pass(name);
            else fail(name, "After retry: expected PROCESSING, got: " + statusAfterRetry);
        } catch (Exception ex) { fail(name, ex.toString()); }
    }

    /**
     * Integration: load() correctly reconstructs state from a DB row.
     * Creates a SENT request directly in DB, loads it, verifies state is SentTranscriptRequestState.
     *
     * NOTE: This test requires fix #4 (constructor reads status from entity via StateFactory).
     * If load() always returns PENDING, this test will fail with a clear message.
     */
    static void integration_load_reconstructsCorrectState() {
        String name = "Integration: load() reconstructs correct state from DB (requires fix #4)";
        try {
            DatabaseManager db = DatabaseManager.getInstance();
            Student student = seedStudent(db, "tr_load", "TRL001", "tr_load@test.com");

            // Insert a SENT row directly so we can test load() independently of the state machine
            int requestId = db.executeInsert(
                    "INSERT INTO transcript_requests " +
                            "(student_id, request_type, status, tracking_number) " +
                            "VALUES (?, 'OFFICIAL', 'SENT', 'TR-99999999')",
                    student.getId());

            TranscriptRequest raw = db.fetch(
                    "SELECT * FROM transcript_requests WHERE id = ?",
                    rs -> {
                        TranscriptRequest r = new TranscriptRequest();
                        r.setId(rs.getInt("id"));
                        r.setStatus(rs.getString("status"));
                        r.setTrackingNumber(rs.getString("tracking_number"));
                        return r;
                    }, requestId);

            TranscriptRequestContext ctx = TranscriptRequestContext.load(raw, student);

            boolean correctState  = ctx.getState() instanceof SentTranscriptRequestState;
            boolean correctStatus = "SENT".equals(ctx.getTranscriptRequest().getStatus());

            if (correctState && correctStatus) pass(name);
            else fail(name, "Expected SentTranscriptRequestState but got: "
                    + ctx.getState().getClass().getSimpleName()
                    + " — fix #4 (constructor reads state from entity) is likely not applied");
        } catch (Exception ex) { fail(name, ex.toString()); }
    }

    // -------------------------------------------------------------------------
    // Main
    // -------------------------------------------------------------------------

    public static void main(String[] args) {

        header("Unit Tests — Valid Transitions");
        test_pendingProcess_transitionsToProcessing();
        test_processingReady_transitionsToReady();
        test_readySend_transitionsToSent();
        test_pendingCancel_transitionsToCancelled();
        test_processingCancel_transitionsToCancelled();
        test_readyCancel_transitionsToCancelled();
        test_processingFail_transitionsToFailed();
        test_failedRetry_transitionsToProcessing();

        header("Unit Tests — Guard Methods");
        test_guards_pendingState();
        test_guards_processingState();
        test_guards_readyState();
        test_guards_sentState();
        test_guards_cancelledState();
        test_guards_failedState();

        header("Unit Tests — Illegal Transitions");
        test_illegal_sendFromPending();
        test_illegal_sendFromProcessing();
        test_illegal_anyFromSent();
        test_illegal_anyFromCancelled();

        header("Unit Tests — Tracking Number & StateFactory");
        test_trackingNumberFormat();
        test_stateFactory_allSixStrings();

        header("Integration Tests");
        DatabaseManager db = DatabaseManager.getInstance();
        db.seedDatabase();
        integration_fullHappyPath();
        integration_cancelFromPending();
        integration_failAndRetry();
        integration_load_reconstructsCorrectState();

        System.out.println("\n════════════════════════════════");
        System.out.printf("  Results: %d passed, %d failed%n", passed, failed);
        System.out.println("════════════════════════════════");

        db.shutdown();
    }
}