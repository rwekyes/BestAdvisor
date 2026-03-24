package edu.advising.states.transcriptstates;

import edu.advising.states.TranscriptState;

public class Dismissed implements TranscriptState {
    private static final Dismissed instance = new Dismissed();

    private Dismissed() {

    }

    public static Dismissed getInstance(){
        return instance;
    }
}
