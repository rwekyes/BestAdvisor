package edu.advising.states.transcriptstates;

import edu.advising.states.TranscriptState;

public class WithdrawnT implements TranscriptState {

    private static final WithdrawnT instance = new WithdrawnT();

    private WithdrawnT() {

    }

    public static WithdrawnT getInstance(){
        return instance;
    }
}
