package edu.advising.states.transcriptstates;

import edu.advising.states.TranscriptState;

public class GraduatedTranscriptState implements TranscriptState {

    private static final GraduatedTranscriptState instance = new GraduatedTranscriptState();

    private GraduatedTranscriptState() {

    }

    public static GraduatedTranscriptState getInstance(){
        return instance;
    }
}
