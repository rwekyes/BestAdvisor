package edu.advising.permissions;

import edu.advising.users.User;

public class PermissionTreeFactory {
    public static PermissionTree forUser(User user){
        // constructs the correct base tree for the user's type
        // When the restriction tree is made, a quick helper method is going to map the Group name to the office field in the Restriction
        return null;
    }

    public static void applyHold(PermissionTree tree, String holdType){
        /*
        adds the appropriate hold group to the tree
         */
    }

    public static void removeHold(PermissionTree tree, String holdType){
        /*
        removes the hold group and restores affected permissions
         */
    }

    // Helper that maps an office to a feature code
    private String toOffice(String featureCode){
        switch(featureCode){
            case "FINANCIAL_HOLD" -> {
                return "Bursar's Office";
            }
            case "ACADEMIC_HOLD" -> {
                return "Academic Office";
            }
            case "LIBRARY_HOLD" -> {
                return "Library";
            }
            default -> throw new IllegalStateException("Unknown hold - " + featureCode);
        }
    }
}
