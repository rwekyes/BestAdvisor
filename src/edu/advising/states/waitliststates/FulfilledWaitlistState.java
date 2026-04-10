package edu.advising.states.waitliststates;

import edu.advising.states.WaitlistState;

public class FulfilledWaitlistState implements WaitlistState {

    private static final FulfilledWaitlistState instance = new FulfilledWaitlistState();

    private FulfilledWaitlistState() {

    }

    public static FulfilledWaitlistState getInstance(){
        return instance;
    }
}
