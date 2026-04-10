package edu.advising.states.transcriptstates;

import edu.advising.states.TranscriptState;

public class ProbationTranscriptState implements TranscriptState {

    private static final ProbationTranscriptState instance = new ProbationTranscriptState();

    private ProbationTranscriptState() {

    }

    public static ProbationTranscriptState getInstance(){
        return instance;
    }
}
