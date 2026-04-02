package edu.advising.states.viewstates;

import edu.advising.contexts.ViewContext;
import edu.advising.states.ViewState;

public class RegistrationViewState implements ViewState {

    private RegistrationViewState instance = new RegistrationViewState();

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
        return "REGISTRATION";
    }

    public RegistrationViewState getInstance() {
        return this.instance;
    }
}
