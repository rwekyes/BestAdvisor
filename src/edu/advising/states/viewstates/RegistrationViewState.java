package edu.advising.states.viewstates;

import edu.advising.commands.RegistrationPeriod;
import edu.advising.contexts.RegistrationPeriodContext;
import edu.advising.contexts.ViewContext;
import edu.advising.states.ViewState;

public class RegistrationViewState implements ViewState {

    private static final RegistrationViewState instance = new RegistrationViewState();

    @Override
    public boolean requiresAuthentication() {
        return true;
    }

    public void handleAction(ViewContext ctx, String command, String... params) {
        switch (command) {
            case "CHECK_STATUS" -> render(ctx, params[0], params[1]);
            case "LOGOUT" -> ctx.logout();
            default -> throw new IllegalArgumentException(
                    "Unknown handleAction command - " + command
            );
        };
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

    public void render(ViewContext viewContext, String p1, String p2) {
        RegistrationPeriod currentPeriod = viewContext.getRegistrationPeriodContext().getRegistrationPeriod();
        //TODO: Use the view template object to make the Strings needed
    }

    @Override
    public String getViewName() {
        return "REGISTRATION";
    }

    public static RegistrationViewState getInstance() {
        return instance;
    }
}
