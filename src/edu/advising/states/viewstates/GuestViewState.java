package edu.advising.states.viewstates;

import edu.advising.auth.AuthenticationResult;
import edu.advising.contexts.ViewContext;
import edu.advising.states.ViewState;

public class GuestViewState implements ViewState {

    private static final GuestViewState instance = new GuestViewState();
    @Override
    public boolean requiresAuthentication() {
        return false;
    }

    private GuestViewState(){

    }

    @Override
    public void handleAction() {
        System.out.println("Error - Guest View handleAction call contains no parameters");
    }

    @Override
    public void handleAction(ViewContext viewContext, String command) {

    }

    @Override
    public void handleAction(ViewContext viewContext, String command1, String command2) {

    }

    public void handleAction(ViewContext ctx, String command, String p1, String p2, String p3){
        switch (command) {
            case "LOGIN" -> {
                AuthenticationResult result = ctx.getAuthContext().login(p1, p2, p3);
                if (!result.isFullyAuthenticated()) {
                    System.out.println("Login failed: " + result.getMessage());
                    return;
                }
                ctx.setCurrentUser(result.getUser());
                switch (result.getUser().getUserType()) {
                    case "STUDENT" -> ctx.navigateTo(StudentDashboardViewState.getInstance());
                    case "FACULTY" -> ctx.navigateTo(FacultyDashboardViewState.getInstance());
                    default -> throw new IllegalArgumentException("Unknown userType - " + result.getUser().getUserType());
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

    }

    @Override
    public String getViewName() {
        return "GUEST";
    }

    public static GuestViewState getInstance() {
        return instance;
    }
}
