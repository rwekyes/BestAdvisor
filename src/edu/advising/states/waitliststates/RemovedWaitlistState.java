package edu.advising.states.waitliststates;

import edu.advising.contexts.WaitlistContext;
import edu.advising.states.WaitlistState;

public class RemovedWaitlistState implements WaitlistState {

    private static final RemovedWaitlistState instance = new RemovedWaitlistState();

    private RemovedWaitlistState() {

    }

    public static RemovedWaitlistState getInstance(){
        return instance;
    }

    @Override
    public void offer(WaitlistContext ctx, int expiryHours) {
        throw new IllegalStateException("Cannot offer a REMOVED waitlist entry");
    }

    @Override
    public void accept(WaitlistContext ctx) {
        throw new IllegalStateException("Cannot accept a REMOVED waitlist entry");
    }

    @Override
    public void decline(WaitlistContext ctx) {
        throw new IllegalStateException("Cannot decline a REMOVED waitlist entry");
    }

    @Override
    public void remove(WaitlistContext ctx, String reason) {
        throw new IllegalStateException("Cannot remove a REMOVED waitlist entry");
    }

    @Override
    public void expire(WaitlistContext ctx) {
        throw new IllegalStateException("Cannot expire a REMOVED waitlist entry");
    }

    @Override
    public boolean isActivelyWaiting() {
        return false;
    }

    @Override
    public String getStatusName() {
        return "REMOVED";
    }
}
