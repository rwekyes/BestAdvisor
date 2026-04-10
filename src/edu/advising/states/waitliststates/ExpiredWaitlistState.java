package edu.advising.states.waitliststates;

import edu.advising.states.WaitlistState;

public class ExpiredWaitlistState implements WaitlistState {

    private static final ExpiredWaitlistState instance = new ExpiredWaitlistState();

    private ExpiredWaitlistState() {

    }

    public static ExpiredWaitlistState getInstance(){
        return instance;
    }
}
