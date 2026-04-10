package edu.advising.states.viewstates;

import edu.advising.contexts.ViewContext;
import edu.advising.states.ViewState;

public class PermissionManagementViewState implements ViewState {

    private static final PermissionManagementViewState instance = new PermissionManagementViewState();

    @Override
    public boolean requiresAuthentication() {
        return true;
    }

    @Override
    public void handleAction() {
        System.out.println("Error - PermissionManagement View handleAction call contains no parameters");
    }

    @Override
    public void handleAction(ViewContext viewContext, String login, String jsmith, String s, String string) {
        System.out.println("Error - PermissionManagement View handleAction call contains too many parameters");
    }

    public void handleAction(ViewContext ctx, String command){
        switch(command){
            case "LOGOUT" -> ctx.logout();
            default -> throw new IllegalArgumentException("Unknown handleAction command - " + command);
        }
    }

    public void handleAction(ViewContext ctx, String command1, String command2) {

        switch (command1) {
            case "APPROVE" -> ctx.getFacultyPermissionContext().approve(command2);
            default -> throw new IllegalArgumentException("Unknown handleAction command1 - " + command1);
        }

    }

    @Override
    public void enter(ViewContext viewContext) {

    }

    @Override
    public void exit(ViewContext viewContext) {

    }

    @Override
    public void render(ViewContext viewContext) {

    }

    @Override
    public String getViewName() {
        return "PERMISSION_MANAGEMENT";
    }

    public static PermissionManagementViewState getInstance() {
        return instance;
    }
}
