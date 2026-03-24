package edu.advising.contexts;

import edu.advising.states.StateFactory;
import edu.advising.states.TranscriptState;

public class TranscriptContext {

    private TranscriptState state;

    public TranscriptContext load(String s){
        TranscriptContext context = new TranscriptContext();
        context.state = (TranscriptState) StateFactory.transcriptStateFor(s);
        return context;
    }

    public void updateState(TranscriptState s) {
        this.state = s;
    }

    public TranscriptState getState(){
        return state;
    }
}
