package edu.advising.contexts;

import edu.advising.states.StateFactory;
import edu.advising.states.WaitlistState;

public class WaitlistContext {

    private WaitlistState state;

    public WaitlistContext load(String s){
        WaitlistContext context = new WaitlistContext();
        context.state = (WaitlistState) StateFactory.waitlistStateFor(s);
        return context;
    }

    public void updateState(WaitlistState s) {
        this.state = s;
    }

    public WaitlistState getState() {
        return state;
    }
}
