package edu.advising.states.transcriptstates;

import edu.advising.states.TranscriptState;

public class GoodStanding implements TranscriptState {
    private static final GoodStanding instance = new GoodStanding();

    private GoodStanding() {

    }

    public static GoodStanding getInstance(){
        return instance;
    }
}
