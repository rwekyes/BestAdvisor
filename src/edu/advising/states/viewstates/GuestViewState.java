package edu.advising.states.viewstates;

import edu.advising.auth.AuthenticationResult;
import edu.advising.contexts.ViewContext;
import edu.advising.permissions.PermissionTreeFactory;
import edu.advising.states.ViewState;


public class GuestViewState implements ViewState {

    private static final GuestViewState instance = new GuestViewState();
    @Override
    public boolean requiresAuthentication() {
        return false;
    }

    private GuestViewState(){

    }

    public void handleAction(ViewContext ctx, String command, String... params){
        switch (command) {
            case "LOGIN" -> {
                AuthenticationResult result = ctx.getAuthContext().login(params[0], params[1], params[3]);
                if (!result.isFullyAuthenticated()) {
                    System.out.println("Login failed: " + result.getMessage());
                    return;
                }
                ctx.setCurrentUser(result.getUser());

                ctx.setPermissionTree(PermissionTreeFactory.forUser(result.getUser()));

                if(ctx.getPermissionTree() == null || ctx.getPermissionTree().getChildren().isEmpty()) {
                    System.err.println("Failed to create permission tree for user - " + result.getUser().getUsername());
                    return;
                }

                switch (result.getUser().getUserType()) {
                    case "STUDENT" -> ctx.navigateTo(StudentDashboardViewState.getInstance());
                    case "FACULTY" -> ctx.navigateTo(FacultyDashboardViewState.getInstance());
                    case "ADMIN" -> ctx.navigateTo(AdminDashboardViewState.getInstance());
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
        System.out.println();
        System.out.println("  ================================================");
        System.out.println("         BetterAdvisor  —  Advising System");
        System.out.println("  ================================================");
        System.out.println();
        System.out.println("    [l] Login");
        System.out.println("    [q] Quit");
    }

    @Override
    public String getViewName() {
        return "GUEST";
    }

    public static GuestViewState getInstance() {
        return instance;
    }
}
