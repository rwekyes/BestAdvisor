package edu.advising.states.waitliststates;

import edu.advising.states.WaitlistState;

public class Expired implements WaitlistState {

    private static final Expired instance = new Expired();

    private Expired() {

    }

    public static Expired getInstance(){
        return instance;
    }
}
