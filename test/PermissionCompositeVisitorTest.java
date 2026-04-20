import edu.advising.core.DatabaseManager;
import edu.advising.permissions.*;
import edu.advising.users.Student;

import java.sql.SQLException;

/**
 * Tests for the Composite + Visitor permission system.
 *
 * Unit tests build trees manually in memory — no database required.
 * Integration tests marked [DB] require a running database and will
 * insert/clean up their own test rows.
 */
public class PermissionCompositeVisitorTest {

    private static int passed = 0;
    private static int failed = 0;

    public static void main(String[] args) {
        main();
    }

    static void main() {

        // ── Unit: PermissionCheckVisitor ─────────────────────────────────────
        header("Unit Tests — PermissionCheckVisitor");
        unit_granted_featureCode_returnsTrue();
        unit_unknown_featureCode_returnsFalse();
        unit_denied_featureCode_returnsFalse();
        unit_financialHold_blocksRegisterCourses();
        unit_financialHold_blocksOrderTranscript();
        unit_academicHold_blocksRegisterCourses();
        unit_academicHold_doesNotBlockOrderTranscript();
        unit_libraryHold_blocksOrderTranscript();
        unit_libraryHold_doesNotBlockRegisterCourses();
        unit_holdRemoved_permissionRestored();

        // ── Unit: RestrictionListVisitor ─────────────────────────────────────
        header("Unit Tests — RestrictionListVisitor");
        unit_noHolds_emptySummary();
        unit_singleHold_appearsInSummary();
        unit_singleHold_hasCorrectOffice();
        unit_singleHold_hasCorrectResolution();
        unit_multipleHolds_allAppearInSummary();
        unit_nonHoldDenied_notInSummary();

        // ── Unit: PermissionTree convenience methods ─────────────────────────
        header("Unit Tests — PermissionTree");
        unit_explainDenial_returnsSourceMessage();
        unit_explainDenial_unknownCode_returnsFallback();
        unit_hasPermission_usesVisitorNotDirectIteration();

        // ── Integration: applyHold and removeHold persist to DB ──────────────
        header("Integration Tests [DB] — Hold Persistence");
        integration_applyHold_insertsUserRolesRow();
        integration_removeHold_deactivatesUserRolesRow();

        // ── Summary ──────────────────────────────────────────────────────────
        System.out.println("\n─────────────────────────────────────────");
        System.out.printf("  Results: %d passed, %d failed%n", passed, failed);
        System.out.println("─────────────────────────────────────────");
    }

    // =========================================================================
    // Helpers
    // =========================================================================

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

    /** Builds a minimal student base tree in memory — no DB. */
    private static PermissionTree buildStudentTree() {
        PermissionTree tree = new PermissionTree();
        PermissionGroup base = new PermissionGroup(GroupNames.STUDENT_BASE);
        base.addChild(new FeaturePermission(GroupNames.STUDENT_BASE, FeatureCodes.REGISTER_COURSES, true, null));
        base.addChild(new FeaturePermission(GroupNames.STUDENT_BASE, FeatureCodes.VIEW_GRADES,       true, null));
        base.addChild(new FeaturePermission(GroupNames.STUDENT_BASE, FeatureCodes.ORDER_TRANSCRIPT,  true, null));
        base.addChild(new FeaturePermission(GroupNames.STUDENT_BASE, FeatureCodes.VIEW_FINANCIAL,    true, null));
        tree.add(base);
        return tree;
    }

    private static void applyFinancialHold(PermissionTree tree) {
        PermissionGroup hold = new PermissionGroup(GroupNames.FINANCIAL_HOLD);
        hold.addChild(new FeaturePermission(GroupNames.FINANCIAL_HOLD, FeatureCodes.REGISTER_COURSES, false,
                "Contact the Bursar's office to resolve a financial hold"));
        hold.addChild(new FeaturePermission(GroupNames.FINANCIAL_HOLD, FeatureCodes.ORDER_TRANSCRIPT, false,
                "Contact the Bursar's office to resolve a financial hold"));
        tree.add(hold);
    }

    private static void applyAcademicHold(PermissionTree tree) {
        PermissionGroup hold = new PermissionGroup(GroupNames.ACADEMIC_HOLD);
        hold.addChild(new FeaturePermission(GroupNames.ACADEMIC_HOLD, FeatureCodes.REGISTER_COURSES, false,
                "Contact Academic Affairs to resolve an academic hold"));
        tree.add(hold);
    }

    private static void applyLibraryHold(PermissionTree tree) {
        PermissionGroup hold = new PermissionGroup(GroupNames.LIBRARY_HOLD);
        hold.addChild(new FeaturePermission(GroupNames.LIBRARY_HOLD, FeatureCodes.ORDER_TRANSCRIPT, false,
                "Contact the Library to resolve an outstanding balance"));
        tree.add(hold);
    }

    // =========================================================================
    // Unit — PermissionCheckVisitor
    // =========================================================================

    static void unit_granted_featureCode_returnsTrue() {
        String name = "Unit: granted feature code returns true";
        try {
            PermissionTree tree = buildStudentTree();
            if (tree.hasPermission(FeatureCodes.REGISTER_COURSES)) pass(name);
            else fail(name, "expected true for REGISTER_COURSES on student base tree");
        } catch (Exception e) { fail(name, e.toString()); }
    }

    static void unit_unknown_featureCode_returnsFalse() {
        String name = "Unit: unknown feature code returns false";
        try {
            PermissionTree tree = buildStudentTree();
            if (!tree.hasPermission("NONEXISTENT_CODE")) pass(name);
            else fail(name, "expected false for unknown feature code");
        } catch (Exception e) { fail(name, e.toString()); }
    }

    static void unit_denied_featureCode_returnsFalse() {
        String name = "Unit: explicitly denied feature code returns false";
        try {
            PermissionTree tree = new PermissionTree();
            PermissionGroup base = new PermissionGroup(GroupNames.STUDENT_BASE);
            base.addChild(new FeaturePermission(GroupNames.STUDENT_BASE, FeatureCodes.SUBMIT_GRADES, false, null));
            tree.add(base);
            if (!tree.hasPermission(FeatureCodes.SUBMIT_GRADES)) pass(name);
            else fail(name, "expected false for explicitly denied feature");
        } catch (Exception e) { fail(name, e.toString()); }
    }

    static void unit_financialHold_blocksRegisterCourses() {
        String name = "Unit: FINANCIAL_HOLD blocks REGISTER_COURSES";
        try {
            PermissionTree tree = buildStudentTree();
            applyFinancialHold(tree);
            if (!tree.hasPermission(FeatureCodes.REGISTER_COURSES)) pass(name);
            else fail(name, "FINANCIAL_HOLD should block REGISTER_COURSES");
        } catch (Exception e) { fail(name, e.toString()); }
    }

    static void unit_financialHold_blocksOrderTranscript() {
        String name = "Unit: FINANCIAL_HOLD blocks ORDER_TRANSCRIPT";
        try {
            PermissionTree tree = buildStudentTree();
            applyFinancialHold(tree);
            if (!tree.hasPermission(FeatureCodes.ORDER_TRANSCRIPT)) pass(name);
            else fail(name, "FINANCIAL_HOLD should block ORDER_TRANSCRIPT");
        } catch (Exception e) { fail(name, e.toString()); }
    }

    static void unit_academicHold_blocksRegisterCourses() {
        String name = "Unit: ACADEMIC_HOLD blocks REGISTER_COURSES";
        try {
            PermissionTree tree = buildStudentTree();
            applyAcademicHold(tree);
            if (!tree.hasPermission(FeatureCodes.REGISTER_COURSES)) pass(name);
            else fail(name, "ACADEMIC_HOLD should block REGISTER_COURSES");
        } catch (Exception e) { fail(name, e.toString()); }
    }

    static void unit_academicHold_doesNotBlockOrderTranscript() {
        String name = "Unit: ACADEMIC_HOLD does not block ORDER_TRANSCRIPT";
        try {
            PermissionTree tree = buildStudentTree();
            applyAcademicHold(tree);
            if (tree.hasPermission(FeatureCodes.ORDER_TRANSCRIPT)) pass(name);
            else fail(name, "ACADEMIC_HOLD should not block ORDER_TRANSCRIPT");
        } catch (Exception e) { fail(name, e.toString()); }
    }

    static void unit_libraryHold_blocksOrderTranscript() {
        String name = "Unit: LIBRARY_HOLD blocks ORDER_TRANSCRIPT";
        try {
            PermissionTree tree = buildStudentTree();
            applyLibraryHold(tree);
            if (!tree.hasPermission(FeatureCodes.ORDER_TRANSCRIPT)) pass(name);
            else fail(name, "LIBRARY_HOLD should block ORDER_TRANSCRIPT");
        } catch (Exception e) { fail(name, e.toString()); }
    }

    static void unit_libraryHold_doesNotBlockRegisterCourses() {
        String name = "Unit: LIBRARY_HOLD does not block REGISTER_COURSES";
        try {
            PermissionTree tree = buildStudentTree();
            applyLibraryHold(tree);
            if (tree.hasPermission(FeatureCodes.REGISTER_COURSES)) pass(name);
            else fail(name, "LIBRARY_HOLD should not block REGISTER_COURSES");
        } catch (Exception e) { fail(name, e.toString()); }
    }

    static void unit_holdRemoved_permissionRestored() {
        String name = "Unit: removing FINANCIAL_HOLD restores REGISTER_COURSES";
        try {
            PermissionTree tree = buildStudentTree();
            applyFinancialHold(tree);
            // Confirm it's blocked first
            if (tree.hasPermission(FeatureCodes.REGISTER_COURSES)) {
                fail(name, "hold should be blocking before removal");
                return;
            }
            // Remove in-memory (no DB call needed for unit test)
            tree.getChildren().removeIf(c -> c instanceof PermissionGroup g
                    && g.getName().equals(GroupNames.FINANCIAL_HOLD));
            if (tree.hasPermission(FeatureCodes.REGISTER_COURSES)) pass(name);
            else fail(name, "REGISTER_COURSES should be restored after hold removal");
        } catch (Exception e) { fail(name, e.toString()); }
    }

    // =========================================================================
    // Unit — RestrictionListVisitor
    // =========================================================================

    static void unit_noHolds_emptySummary() {
        String name = "Unit: tree with no holds returns empty RestrictionSummary";
        try {
            PermissionTree tree = buildStudentTree();
            RestrictionSummary summary = tree.getRestrictions();
            if (!summary.hasRestrictions()) pass(name);
            else fail(name, "expected empty summary, got " + summary.getHolds().size() + " holds");
        } catch (Exception e) { fail(name, e.toString()); }
    }

    static void unit_singleHold_appearsInSummary() {
        String name = "Unit: FINANCIAL_HOLD appears in RestrictionSummary";
        try {
            PermissionTree tree = buildStudentTree();
            applyFinancialHold(tree);
            RestrictionSummary summary = tree.getRestrictions();
            if (summary.hasRestrictions()) pass(name);
            else fail(name, "expected at least one restriction in summary");
        } catch (Exception e) { fail(name, e.toString()); }
    }

    static void unit_singleHold_hasCorrectOffice() {
        String name = "Unit: FINANCIAL_HOLD restriction has correct office";
        try {
            PermissionTree tree = buildStudentTree();
            applyFinancialHold(tree);
            RestrictionSummary summary = tree.getRestrictions();
            boolean found = summary.getHolds().stream()
                    .anyMatch(r -> "Bursar's Office".equals(r.office));
            if (found) pass(name);
            else fail(name, "expected office 'Bursar's Office', got: " +
                    summary.getHolds().stream().map(r -> r.office).toList());
        } catch (Exception e) { fail(name, e.toString()); }
    }

    static void unit_singleHold_hasCorrectResolution() {
        String name = "Unit: FINANCIAL_HOLD restriction has correct resolution message";
        try {
            PermissionTree tree = buildStudentTree();
            applyFinancialHold(tree);
            RestrictionSummary summary = tree.getRestrictions();
            boolean found = summary.getHolds().stream()
                    .anyMatch(r -> r.resolution != null &&
                            r.resolution.contains("Bursar"));
            if (found) pass(name);
            else fail(name, "expected Bursar resolution message in summary");
        } catch (Exception e) { fail(name, e.toString()); }
    }

    static void unit_multipleHolds_allAppearInSummary() {
        String name = "Unit: FINANCIAL_HOLD + LIBRARY_HOLD both appear in summary";
        try {
            PermissionTree tree = buildStudentTree();
            applyFinancialHold(tree);
            applyLibraryHold(tree);
            RestrictionSummary summary = tree.getRestrictions();
            // FINANCIAL_HOLD has 2 denied leaves, LIBRARY_HOLD has 1 — expect 3 entries
            if (summary.getHolds().size() == 3) pass(name);
            else fail(name, "expected 3 restriction entries, got " + summary.getHolds().size());
        } catch (Exception e) { fail(name, e.toString()); }
    }

    static void unit_nonHoldDenied_notInSummary() {
        String name = "Unit: denied permission in non-hold group does not appear in summary";
        try {
            PermissionTree tree = new PermissionTree();
            PermissionGroup base = new PermissionGroup(GroupNames.STUDENT_BASE);
            // Students can't submit grades — denied in base group
            base.addChild(new FeaturePermission(GroupNames.STUDENT_BASE, FeatureCodes.SUBMIT_GRADES, false, null));
            tree.add(base);
            RestrictionSummary summary = tree.getRestrictions();
            if (!summary.hasRestrictions()) pass(name);
            else fail(name, "base group denials should not appear as holds");
        } catch (Exception e) { fail(name, e.toString()); }
    }

    // =========================================================================
    // Unit — PermissionTree convenience methods
    // =========================================================================

    static void unit_explainDenial_returnsSourceMessage() {
        String name = "Unit: explainDenial returns source message for blocked feature";
        try {
            PermissionTree tree = buildStudentTree();
            applyFinancialHold(tree);
            String msg = tree.explainDenial(FeatureCodes.REGISTER_COURSES);
            if (msg != null && msg.contains("Bursar")) pass(name);
            else fail(name, "expected Bursar message, got: " + msg);
        } catch (Exception e) { fail(name, e.toString()); }
    }

    static void unit_explainDenial_unknownCode_returnsFallback() {
        String name = "Unit: explainDenial returns fallback for unknown feature code";
        try {
            PermissionTree tree = buildStudentTree();
            String msg = tree.explainDenial("NONEXISTENT");
            if (msg != null && msg.contains("Unknown")) pass(name);
            else fail(name, "expected fallback message, got: " + msg);
        } catch (Exception e) { fail(name, e.toString()); }
    }

    static void unit_hasPermission_usesVisitorNotDirectIteration() {
        String name = "Unit: hasPermission traverses into group children (not just top-level)";
        try {
            // A feature nested two levels deep should still be found
            PermissionTree tree = new PermissionTree();
            PermissionGroup outer = new PermissionGroup(GroupNames.HONORS_ADDON);
            outer.addChild(new FeaturePermission(GroupNames.HONORS_ADDON, FeatureCodes.VIEW_BUDGET, true, null));
            tree.add(outer);
            if (tree.hasPermission(FeatureCodes.VIEW_BUDGET)) pass(name);
            else fail(name, "visitor should traverse into group children");
        } catch (Exception e) { fail(name, e.toString()); }
    }

    // =========================================================================
    // Integration — DB persistence
    // Prerequisite: running database with schema initialized.
    // Each test inserts its own rows and cleans up after itself.
    // =========================================================================

    static void integration_applyHold_insertsUserRolesRow() {
        String name = "Integration [DB]: applyHold inserts active row in user_roles";
        int userId = -1;
        try {
            DatabaseManager db = DatabaseManager.getInstance();

            userId = db.executeInsert(
                    "INSERT INTO users (username, password, user_type, first_name, last_name, email) " +
                            "VALUES ('hold_test_user', 'pw', 'STUDENT', 'Hold', 'Test', 'hold@test.com')");
            db.executeInsert("INSERT INTO students (id, student_id) VALUES (?, 'HT001')", userId);

            // Build an in-memory tree tied to this user
            edu.advising.users.Student student =
                    db.fetchOne(edu.advising.users.Student.class, "id", userId);
            PermissionTree tree = new PermissionTree();
            tree.add(new PermissionGroup(GroupNames.STUDENT_BASE)); // placeholder so tree isn't empty
            // Manually set user via a wrapper trick — applyHold needs getUser()
            PermissionTree treeWithUser = new PermissionTree(student);

            PermissionTreeFactory.applyHold(treeWithUser, GroupNames.FINANCIAL_HOLD);

            int count = db.executeQuery(
                    "SELECT COUNT(*) FROM user_roles WHERE user_id = ? AND role_name = ? AND is_active = TRUE",
                    rs -> rs.next() ? rs.getInt(1) : 0,
                    userId, GroupNames.FINANCIAL_HOLD);

            if (count == 1) pass(name);
            else fail(name, "expected 1 active user_roles row, found " + count);

        } catch (Exception e) {
            fail(name, e.toString());
        } finally {
            cleanupUser(userId);
        }
    }

    static void integration_removeHold_deactivatesUserRolesRow() {
        String name = "Integration [DB]: removeHold sets is_active = FALSE in user_roles";
        int userId = -1;
        try {
            DatabaseManager db = DatabaseManager.getInstance();

            userId = db.executeInsert(
                    "INSERT INTO users (username, password, user_type, first_name, last_name, email) " +
                            "VALUES ('remove_hold_user', 'pw', 'STUDENT', 'Remove', 'Test', 'remove@test.com')");
            db.executeInsert("INSERT INTO students (id, student_id) VALUES (?, 'RH001')", userId);

            edu.advising.users.Student student =
                    db.fetchOne(edu.advising.users.Student.class, "id", userId);
            PermissionTree tree = new PermissionTree(student);

            // Apply then remove
            PermissionTreeFactory.applyHold(tree, GroupNames.FINANCIAL_HOLD);
            PermissionTreeFactory.removeHold(tree, GroupNames.FINANCIAL_HOLD);

            int activeCount = db.executeQuery(
                    "SELECT COUNT(*) FROM user_roles WHERE user_id = ? AND role_name = ? AND is_active = TRUE",
                    rs -> rs.next() ? rs.getInt(1) : 0,
                    userId, GroupNames.FINANCIAL_HOLD);

            if (activeCount == 0) pass(name);
            else fail(name, "expected is_active = FALSE after removeHold, found " + activeCount + " active rows");

        } catch (Exception e) {
            fail(name, e.toString());
        } finally {
            cleanupUser(userId);
        }
    }

    private static void cleanupUser(int userId) {
        if (userId < 0) return;
        try {
            DatabaseManager db = DatabaseManager.getInstance();
            db.executeUpdate("DELETE FROM user_roles WHERE user_id = ?", userId);
            db.executeUpdate("DELETE FROM students WHERE id = ?", userId);
            db.executeUpdate("DELETE FROM users WHERE id = ?", userId);
        } catch (SQLException e) {
            System.err.println("  [cleanup failed for userId=" + userId + ": " + e.getMessage() + "]");
        }
    }
}
