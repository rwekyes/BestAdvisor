package edu.advising.states.viewstates;

import edu.advising.contexts.ViewContext;
import edu.advising.states.ViewState;

public class TranscriptViewState implements ViewState {

    private static TranscriptViewState instance = new TranscriptViewState();

    @Override
    public boolean requiresAuthentication() {
        return true;
    }

    @Override
    public void handleAction() {
        System.out.println("Error - Transcript View handleAction call contains no parameters");
    }

    @Override
    public void handleAction(ViewContext viewContext, String command, String p1, String p2, String p3) {
        System.out.println("Error - Transcript View handleAction call contains too many parameters");
    }

    @Override
    public void handleAction(ViewContext ctx, String command){
        switch(command){
            case "LOGOUT" -> ctx.logout();
            default -> throw new IllegalArgumentException("Unknown handleAction command - " + command);
        }
    }

    @Override
    public void handleAction(ViewContext viewContext, String command1, String command2) {

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
        return "TRANSCRIPT_VIEW";
    }

    public static TranscriptViewState getInstance() {
        return instance;
    }

}
