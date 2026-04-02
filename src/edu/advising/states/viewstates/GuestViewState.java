package edu.advising.states.viewstates;

import edu.advising.auth.AuthenticationContext;
import edu.advising.contexts.ViewContext;
import edu.advising.states.ViewState;

public class GuestViewState implements ViewState {

    private AuthenticationContext authContext;
    private static GuestViewState instance = new GuestViewState();
    @Override
    public boolean requiresAuthentication() {
        return false;
    }

    @Override
    public void handleAction() {

    }

    public void handleAction(String command, String p1, String p2, String p3){
        switch (command) {
            case "LOGIN" -> authContext.login(p1,p2,p3);
            case "LOGOUT" -> authContext.logout();
            default -> throw new IllegalArgumentException(
                    "Unknown handleAction command - " + command + " - "
            );
        };
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
        return "GUEST";
    }

    public static GuestViewState getInstance() {
        return instance;
    }
}
