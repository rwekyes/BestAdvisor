package edu.advising.states.transcriptstates;

import edu.advising.states.TranscriptState;

public class GoodStandingTranscriptState implements TranscriptState {
    private static final GoodStandingTranscriptState instance = new GoodStandingTranscriptState();

    private GoodStandingTranscriptState() {

    }

    public static GoodStandingTranscriptState getInstance(){
        return instance;
    }
}
