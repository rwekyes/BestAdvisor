package edu.advising.states.waitliststates;

import edu.advising.contexts.WaitlistContext;
import edu.advising.states.WaitlistState;

import java.time.LocalDateTime;

public class ActiveWaitlistState implements WaitlistState {

    private static final ActiveWaitlistState instance = new ActiveWaitlistState();

    private ActiveWaitlistState() {

    }

    public static ActiveWaitlistState getInstance(){
        return instance;
    }

    @Override
    public void offer(WaitlistContext ctx, int expiryHours) {
        ctx.getEntry().setRemovedDate(LocalDateTime.now().plusHours(expiryHours));
        ctx.getEntry().setNotificationSent(true);
        ctx.setState(OfferedWaitlistState.getInstance());
        ctx.persist();
        ctx.getNotificationManager().notifyWaitlistUpdate(
                ctx.getStudent(), ctx.getSection().getCourseCode(), ctx.getEntry().getPosition());
    }

    @Override
    public void accept(WaitlistContext ctx) {
        throw new IllegalStateException("Cannot accept an ACTIVE waitlist entry");
    }

    @Override
    public void decline(WaitlistContext ctx) {
        throw new IllegalStateException("Cannot decline an ACTIVE waitlist entry");
    }

    @Override
    public void remove(WaitlistContext ctx, String reason) {
        ctx.getEntry().setRemoveReason(reason);
        ctx.getEntry().setRemovedDate(LocalDateTime.now());
        ctx.setState(RemovedWaitlistState.getInstance());
        // Remove that student from the waitlist
        ctx.getSection().removeFromWaitlist(ctx.getStudent());
        ctx.persist();
        ctx.getNotificationManager().notifyWaitlistUpdate(
                ctx.getStudent(), ctx.getSection().getCourseCode(), ctx.getEntry().getPosition());
    }

    @Override
    public void expire(WaitlistContext ctx) {
        ctx.setState(ExpiredWaitlistState.getInstance());
        // Remove that student from the waitlist
        ctx.getSection().removeFromWaitlist(ctx.getStudent());
        ctx.persist();
        ctx.getNotificationManager().notifyWaitlistUpdate(
                ctx.getStudent(), ctx.getSection().getCourseCode(), ctx.getEntry().getPosition());
    }

    @Override
    public boolean isActivelyWaiting() {
        return true;
    }

    @Override
    public String getStatusName() {
        return "ACTIVE";
    }
}
