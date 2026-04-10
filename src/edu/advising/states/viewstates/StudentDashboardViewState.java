package edu.advising.states.viewstates;

import edu.advising.contexts.ViewContext;
import edu.advising.states.ViewState;

public class StudentDashboardViewState implements ViewState {

    private static final StudentDashboardViewState instance = new StudentDashboardViewState();

    @Override
    public boolean requiresAuthentication() {
        return true;
    }

    @Override
    public void handleAction(ViewContext viewContext, String login, String jsmith, String s, String string) {
        System.out.println("Error - Student View handleAction call contains too many parameters");
    }

    public void handleAction() {
        System.out.println("Error - Student View handleAction call contains no parameters");
    }

    public void handleAction(ViewContext ctx, String command){
        switch(command){
            case "LOGOUT" -> ctx.logout();
            default -> throw new IllegalArgumentException("Unknown handleAction command - " + command);
        }
    }

    public void handleAction(ViewContext ctx, String command1, String command2) {
        if(command1 == "NAVIGATE") {
            switch (command2) {
                case "REGISTRATION" -> ctx.navigateTo(RegistrationViewState.getInstance());
                case "TRANSCRIPT" -> ctx.navigateTo(TranscriptViewState.getInstance());
                default -> throw new IllegalArgumentException("Unknown handleAction command2 - " + command2);
            }
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

    }

    @Override
    public String getViewName() {
        return "STUDENT_DASHBOARD";
    }

    public static StudentDashboardViewState getInstance() {
        return instance;
    }
}
