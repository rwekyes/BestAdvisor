package edu.advising.states.viewstates;

import edu.advising.contexts.ViewContext;
import edu.advising.states.ViewState;

public class TranscriptViewState implements ViewState {

    private static final TranscriptViewState instance = new TranscriptViewState();

    @Override
    public boolean requiresAuthentication() {
        return true;
    }

    @Override
    public void handleAction(ViewContext ctx, String command, String... params){
        switch(command){
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

    }

    @Override
    public String getViewName() {
        return "TRANSCRIPT_VIEW";
    }

    public static TranscriptViewState getInstance() {
        return instance;
    }

}
