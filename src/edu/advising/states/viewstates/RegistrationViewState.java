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

    @Override
    public void handleAction() {
        System.out.println("Error - Registration View handleAction call contains no parameters");
    }

    @Override
    public void handleAction(ViewContext viewContext, String login, String jsmith, String s, String string) {

    }

    public void handleAction(ViewContext ctx, String command){
        switch(command){
            case "LOGOUT" -> ctx.logout();
            default -> throw new IllegalArgumentException("Unknown handleAction command - " + command);
        }
    }

    @Override
    public void handleAction(ViewContext viewContext, String command1, String command2) {

    }

    public void handleAction(ViewContext ctx, String command, String p1, String p2) {
        switch (command) {
            case "CHECK_STATUS" -> render(ctx, p1, p2);
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
