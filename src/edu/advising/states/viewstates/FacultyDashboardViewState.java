package edu.advising.states.viewstates;

import edu.advising.contexts.ViewContext;
import edu.advising.states.ViewState;

public class FacultyDashboardViewState implements ViewState {
    private FacultyDashboardViewState instance = new FacultyDashboardViewState();

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
        return "FACULTY_DASHBOARD";
    }

    public FacultyDashboardViewState getInstance() {
        return this.instance;
    }
}
