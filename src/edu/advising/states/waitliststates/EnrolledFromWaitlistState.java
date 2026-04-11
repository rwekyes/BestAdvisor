package edu.advising.states.waitliststates;

import edu.advising.contexts.WaitlistContext;
import edu.advising.states.WaitlistState;

public class EnrolledFromWaitlistState implements WaitlistState {

    private static final EnrolledFromWaitlistState instance = new EnrolledFromWaitlistState();

    private EnrolledFromWaitlistState() {

    }

    public static EnrolledFromWaitlistState getInstance(){
        return instance;
    }

    @Override
    public void offer(WaitlistContext ctx, int expiryHours) {
        throw new IllegalStateException("Cannot offer an ENROLLED waitlist entry");
    }

    @Override
    public void accept(WaitlistContext ctx) {
        throw new IllegalStateException("Cannot accept an ENROLLED waitlist entry");
    }

    @Override
    public void decline(WaitlistContext ctx) {
        throw new IllegalStateException("Cannot decline an ENROLLED waitlist entry");
    }

    @Override
    public void remove(WaitlistContext ctx, String reason) {
        throw new IllegalStateException("Cannot remove an ENROLLED waitlist entry");
    }

    @Override
    public void expire(WaitlistContext ctx) {
        throw new IllegalStateException("Cannot expire an ENROLLED waitlist entry");
    }

    @Override
    public boolean isActivelyWaiting() {
        return false;
    }

    @Override
    public String getStatusName() {
        return "ENROLLED";
    }
}
