package edu.advising.states.viewstates;

import edu.advising.contexts.ViewContext;
import edu.advising.states.ViewState;

public class AdminDashboardViewState implements ViewState {

    private static final AdminDashboardViewState instance = new AdminDashboardViewState();

    @Override
    public boolean requiresAuthentication() {
        return true;
    }


    @Override
    public void handleAction(ViewContext ctx, String command, String... params) {
        switch(command){
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
        return "ADMIN_DASHBOARD";
    }

    public static AdminDashboardViewState getInstance() {
        return instance;
    }
}
