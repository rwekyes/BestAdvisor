package edu.advising.states.transcriptstates;

import edu.advising.states.TranscriptState;

public class AcademicDisqualification implements TranscriptState {
    private static final AcademicDisqualification instance = new AcademicDisqualification();

    private AcademicDisqualification() {

    }

    public static AcademicDisqualification getInstance(){
        return instance;
    }
}
