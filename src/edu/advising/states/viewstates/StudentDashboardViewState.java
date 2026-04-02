package edu.advising.states.viewstates;

import edu.advising.contexts.ViewContext;
import edu.advising.states.ViewState;

public class StudentDashboardViewState implements ViewState {

    private StudentDashboardViewState instance = new StudentDashboardViewState();

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
        return "STUDENT_DASHBOARD";
    }

    public StudentDashboardViewState getInstance() {
        return this.instance;
    }
}
