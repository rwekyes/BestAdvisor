package edu.advising.states.viewstates;

import edu.advising.contexts.ViewContext;
import edu.advising.states.ViewState;

public class AdminDashboardViewState implements ViewState {

    private AdminDashboardViewState instance = new AdminDashboardViewState();
    @Override
    public boolean requiresAuthentication() {
        return true;
    }

    @Override
    public void handleAction() {

    }

    @Override
    public void handleAction(ViewContext viewContext, String command) {

    }

    @Override
    public void handleAction(ViewContext viewContext, String command1, String command2) {

    }

    @Override
    public void handleAction(ViewContext viewContext, String login, String jsmith, String s, String string) {

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
        return "ADMIN_DASHBOARD";
    }

    public AdminDashboardViewState getInstance() {
        return this.instance;
    }
}
