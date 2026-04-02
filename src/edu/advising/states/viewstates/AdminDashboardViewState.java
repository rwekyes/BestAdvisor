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
    public void enter(ViewContext viewContext) {

    }

    @Override
    public void exit() {

    }

    @Override
    public void render() {

    }

    @Override
    public String getViewName() {
        return "ADMIN_DASHBOARD";
    }

    public AdminDashboardViewState getInstance() {
        return this.instance;
    }
}
