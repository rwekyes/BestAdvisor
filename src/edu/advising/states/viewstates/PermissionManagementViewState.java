package edu.advising.states.viewstates;

import edu.advising.contexts.ViewContext;
import edu.advising.states.ViewState;

public class PermissionManagementViewState implements ViewState {

    private static final PermissionManagementViewState instance = new PermissionManagementViewState();

    @Override
    public boolean requiresAuthentication() {
        return true;
    }


    public void handleAction(ViewContext ctx, String command, String... params) {
        switch (command) {
            case "APPROVE" -> ctx.getFacultyPermissionContext().approve();
            case "LOGOUT" -> ctx.logout();
            default -> throw new IllegalArgumentException("Unknown handleAction command - " + command);
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
