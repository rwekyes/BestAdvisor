package edu.advising.states.transcriptstates;

import edu.advising.states.TranscriptState;

public class AcademicDisqualificationTranscriptState implements TranscriptState {
    private static final AcademicDisqualificationTranscriptState instance = new AcademicDisqualificationTranscriptState();

    private AcademicDisqualificationTranscriptState() {

    }

    public static AcademicDisqualificationTranscriptState getInstance(){
        return instance;
    }
}
