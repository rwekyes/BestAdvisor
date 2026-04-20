package edu.advising.permissions;

import edu.advising.core.DatabaseManager;
import edu.advising.users.User;

import java.sql.SQLException;

public class PermissionTreeFactory {
    public static PermissionTree forUser(User user) {
        try {
            return new PermissionTree(user);
        } catch (SQLException e) {
            return null;
        }
    }

    public static void applyHold(PermissionTree tree, String holdType) throws SQLException {
        PermissionGroup holdGroup = new PermissionGroup(holdType);

        switch (holdType) {
            case "FINANCIAL_HOLD" -> {
                holdGroup.addChild(new FeaturePermission(holdType, FeatureCodes.REGISTER_COURSES, false,
                        "Contact the Bursar's office to resolve a financial hold"));
                holdGroup.addChild(new FeaturePermission(holdType, FeatureCodes.ORDER_TRANSCRIPT, false,
                        "Contact the Bursar's office to resolve a financial hold"));
            }
            case "ACADEMIC_HOLD" ->
                    holdGroup.addChild(new FeaturePermission(holdType, FeatureCodes.REGISTER_COURSES, false,
                            "Contact Academic Affairs to resolve an academic hold"));
            case "LIBRARY_HOLD" ->
                    holdGroup.addChild(new FeaturePermission(holdType, FeatureCodes.ORDER_TRANSCRIPT, false,
                            "Contact the Library to resolve an outstanding balance"));
            default -> throw new IllegalArgumentException("Unknown hold type: " + holdType);
        }

        DatabaseManager.getInstance().executeUpdate(
                "MERGE INTO user_roles (user_id, role_name, is_active) " +
                        "KEY (user_id, role_name) " +
                        "VALUES (?, ?, TRUE)",
                tree.getUser().getId(), holdType
        );

        tree.add(holdGroup);
    }

    public static void removeHold(PermissionTree tree, String holdType) throws SQLException{
        DatabaseManager.getInstance().executeUpdate(
                "UPDATE user_roles SET is_active = FALSE WHERE user_id = ? AND role_name = ?",
                tree.getUser().getId(), holdType
        );
        tree.getChildren().removeIf(c -> c instanceof PermissionGroup g && g.getName().equals(holdType));
    }

    // Helper that maps a feature code to an office string
    public static String toOffice(String groupName){
        switch(groupName){
            case "FINANCIAL_HOLD" -> {
                return "Bursar's Office";
            }
            case "ACADEMIC_HOLD" -> {
                return "Academic Office";
            }
            case "LIBRARY_HOLD" -> {
                return "Library";
            }
            default -> throw new IllegalStateException("Unknown hold - " + groupName);
        }
    }

    // Seed method for adding a new user into the user_roles table
    public static void seedPermissions(User user) throws SQLException{
        DatabaseManager.getInstance().executeUpdate(
                "MERGE INTO user_roles (user_id, role_name, is_active) " +
                        "KEY (user_id, role_name) " +
                        "VALUES (?, ?, TRUE)",
                user.getId(), baseGroupFor(user.getUserType())
        );
    }

    // Helper for the seed method, public in case we need the group name for a specific user type
    public static String baseGroupFor(String userType){
        switch(userType){
            case "STUDENT" -> {
                return GroupNames.STUDENT_BASE;
            }
            case "FACULTY" -> {
                return GroupNames.FACULTY_BASE;
            }
            case "ADMIN" -> {
                return GroupNames.ADMIN_BASE;
            }
            default -> throw new IllegalStateException("Unknown user type - " + userType);
        }
    }
}
