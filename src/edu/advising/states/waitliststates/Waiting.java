package edu.advising.states.waitliststates;

import edu.advising.states.WaitlistState;

public class Waiting implements WaitlistState {

    private static final Waiting instance = new Waiting();

    private Waiting() {

    }

    public static Waiting getInstance(){
        return instance;
    }
}
