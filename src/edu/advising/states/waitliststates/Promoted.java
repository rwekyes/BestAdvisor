package edu.advising.states.waitliststates;

import edu.advising.states.WaitlistState;

public class Promoted implements WaitlistState {

    private static final Promoted instance = new Promoted();

    private Promoted() {

    }

    public static Promoted getInstance(){
        return instance;
    }
}
