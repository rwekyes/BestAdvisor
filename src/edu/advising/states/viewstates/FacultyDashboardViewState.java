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
    public void handleAction() {
        System.out.println("Error - Faculty View handleAction call contains no parameters");
    }

    @Override
    public void handleAction(ViewContext viewContext, String command, String p1, String p2, String p3) {
        System.out.println("Error - Faculty View handleAction call contains too many parameters");
    }

    public void handleAction(ViewContext ctx, String command){
        switch(command){
            case "LOGOUT" -> ctx.logout();
            default -> throw new IllegalArgumentException("Unknown handleAction command - " + command);
        }
    }

    public void handleAction(ViewContext ctx, String command1, String command2) {
        if(command1 == "NAVIGATE") {
            switch (command2) {
                case "PERMISSIONS" -> ctx.navigateTo(PermissionManagementViewState.getInstance());
                default -> throw new IllegalArgumentException("Unknown handleAction command2 - " + command2);
            }
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
        return "FACULTY_DASHBOARD";
    }

    public static FacultyDashboardViewState getInstance() {
        return instance;
    }
}
