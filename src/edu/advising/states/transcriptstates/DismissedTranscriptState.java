package edu.advising.states.transcriptstates;

import edu.advising.states.TranscriptState;

public class DismissedTranscriptState implements TranscriptState {
    private static final DismissedTranscriptState instance = new DismissedTranscriptState();

    private DismissedTranscriptState() {

    }

    public static DismissedTranscriptState getInstance(){
        return instance;
    }
}
