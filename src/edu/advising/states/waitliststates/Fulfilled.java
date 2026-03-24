package edu.advising.states.waitliststates;

import edu.advising.states.WaitlistState;

public class Fulfilled implements WaitlistState {

    private static final Fulfilled instance = new Fulfilled();

    private Fulfilled() {

    }

    public static Fulfilled getInstance(){
        return instance;
    }
}
