package edu.advising.states.transcriptstates;

import edu.advising.states.TranscriptState;

public class Graduated implements TranscriptState {

    private static final Graduated instance = new Graduated();

    private Graduated() {

    }

    public static Graduated getInstance(){
        return instance;
    }
}
