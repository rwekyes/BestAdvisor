package edu.advising.states.waitliststates;

import edu.advising.contexts.WaitlistContext;
import edu.advising.states.WaitlistState;

public class ExpiredWaitlistState implements WaitlistState {

    private static final ExpiredWaitlistState instance = new ExpiredWaitlistState();

    private ExpiredWaitlistState() {

    }

    public static ExpiredWaitlistState getInstance(){
        return instance;
    }

    @Override
    public void offer(WaitlistContext ctx, int expiryHours) {
        throw new IllegalStateException("Cannot offer an EXPIRED waitlist entry");
    }

    @Override
    public void accept(WaitlistContext ctx) {
        throw new IllegalStateException("Cannot accept an EXPIRED waitlist entry");
    }

    @Override
    public void decline(WaitlistContext ctx) {
        throw new IllegalStateException("Cannot decline an EXPIRED waitlist entry");
    }

    @Override
    public void remove(WaitlistContext ctx, String reason) {
        throw new IllegalStateException("Cannot remove an EXPIRED waitlist entry");
    }

    @Override
    public void expire(WaitlistContext ctx) {
        throw new IllegalStateException("Cannot expire an EXPIRED waitlist entry");
    }

    @Override
    public boolean isActivelyWaiting() {
        return false;
    }

    @Override
    public String getStatusName() {
        return "EXPIRED";
    }
}
