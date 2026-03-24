package edu.advising.states.transcriptstates;

import edu.advising.states.TranscriptState;

public class Probation implements TranscriptState {

    private static final Probation instance = new Probation();

    private Probation() {

    }

    public static Probation getInstance(){
        return instance;
    }
}
