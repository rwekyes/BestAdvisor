package edu.advising.states.viewstates;

import edu.advising.contexts.ViewContext;
import edu.advising.states.ViewState;

public class FacultyDashboardViewState implements ViewState {

    private static final FacultyDashboardViewState instance = new FacultyDashboardViewState();

    @Override
    public boolean requiresAuthentication() {
        return true;
    }

    @Override
    public void handleAction(ViewContext ctx, String command, String... params) {
        switch(command){
            case "NAVIGATE" -> {
                switch (params[0]) {
                    case "PERMISSIONS" -> ctx.navigateTo(PermissionManagementViewState.getInstance());
                    default -> throw new IllegalArgumentException("Unknown handleAction param1 - " + params[0]);
                }
            }
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
        viewContext.getCurrentUser().showDashboard();
    }

    @Override
    public String getViewName() {
        return "FACULTY_DASHBOARD";
    }

    public static FacultyDashboardViewState getInstance() {
        return instance;
    }
}
