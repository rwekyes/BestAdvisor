package edu.advising.states.viewstates;

import edu.advising.contexts.ViewContext;
import edu.advising.states.ViewState;

public class StudentDashboardViewState implements ViewState {

    private static final StudentDashboardViewState instance = new StudentDashboardViewState();

    @Override
    public boolean requiresAuthentication() {
        return true;
    }

    public void handleAction(ViewContext ctx, String command){
        switch(command){
            default -> throw new IllegalArgumentException("Unknown handleAction command - " + command);
        }
    }

    public void handleAction(ViewContext ctx, String command, String... params) {
        switch(command){
            case "NAVIGATE" -> {
                switch (params[0]) {
                    case "REGISTRATION" -> ctx.navigateTo(RegistrationViewState.getInstance());
                    case "TRANSCRIPT" -> ctx.navigateTo(TranscriptViewState.getInstance());
                    default -> throw new IllegalArgumentException("Unknown handleAction parameter - " + params[0]);
                }
            }
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
        return "STUDENT_DASHBOARD";
    }

    public static StudentDashboardViewState getInstance() {
        return instance;
    }
}
