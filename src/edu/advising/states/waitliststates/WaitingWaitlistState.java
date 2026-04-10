package edu.advising.states.waitliststates;

import edu.advising.states.WaitlistState;

public class WaitingWaitlistState implements WaitlistState {

    private static final WaitingWaitlistState instance = new WaitingWaitlistState();

    private WaitingWaitlistState() {

    }

    public static WaitingWaitlistState getInstance(){
        return instance;
    }
}
