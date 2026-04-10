package edu.advising.states.waitliststates;

import edu.advising.states.WaitlistState;

public class PromotedWaitlistState implements WaitlistState {

    private static final PromotedWaitlistState instance = new PromotedWaitlistState();

    private PromotedWaitlistState() {

    }

    public static PromotedWaitlistState getInstance(){
        return instance;
    }
}
