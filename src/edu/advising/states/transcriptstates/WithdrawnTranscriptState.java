package edu.advising.states.transcriptstates;

import edu.advising.states.TranscriptState;

public class WithdrawnTranscriptState implements TranscriptState {

    private static final WithdrawnTranscriptState instance = new WithdrawnTranscriptState();

    private WithdrawnTranscriptState() {

    }

    public static WithdrawnTranscriptState getInstance(){
        return instance;
    }
}
