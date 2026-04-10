package edu.advising.states.waitliststates;

import edu.advising.states.WaitlistState;

public class RemovedWaitlistState implements WaitlistState {

    private static final RemovedWaitlistState instance = new RemovedWaitlistState();

    private RemovedWaitlistState() {

    }

    public static RemovedWaitlistState getInstance(){
        return instance;
    }
}
