package edu.advising.states.viewstates;

import edu.advising.contexts.ViewContext;
import edu.advising.states.ViewState;

public class TranscriptViewState implements ViewState {

    private TranscriptViewState instance = new TranscriptViewState();

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
        return "TRANSCRIPT_VIEW";
    }

    public TranscriptViewState getInstance() {
        return this.instance;
    }

}
