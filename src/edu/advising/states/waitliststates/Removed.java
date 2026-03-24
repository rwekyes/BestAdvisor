package edu.advising.states.waitliststates;

import edu.advising.states.WaitlistState;

public class Removed implements WaitlistState {

    private static final Removed instance = new Removed();

    private Removed() {

    }

    public static Removed getInstance(){
        return instance;
    }
}
