package edu.advising.states.enrollmentstates;

import edu.advising.contexts.EnrollmentContext;
import edu.advising.states.EnrollmentState;

public class WaitlistedEnrollmentState implements EnrollmentState {

    private static final WaitlistedEnrollmentState instance = new WaitlistedEnrollmentState();

    private WaitlistedEnrollmentState() {}

    public static WaitlistedEnrollmentState getInstance(){
        return instance;
    }

    @Override
    public void confirm(EnrollmentContext ctx) {
        throw new IllegalStateException("Cannot confirm a WAITLISTED enrollment");
    }

    @Override
    public void drop(EnrollmentContext ctx) {
        // TODO: After setting up the waitlist state machine, come back and clean up this mess
        throw new UnsupportedOperationException("Waitlist drop not yet implemented");
    }

    @Override
    public void withdraw(EnrollmentContext ctx) {
        // TODO: After setting up the waitlist state machine, come back and clean up this mess
        throw new UnsupportedOperationException("Waitlist withdrawal not yet implemented");
    }

    @Override
    public void complete(EnrollmentContext ctx, String finalGrade) {
        throw new IllegalStateException("Cannot complete a WAITLISTED enrollment");
    }

    @Override
    public void reenroll(EnrollmentContext ctx) {
        throw new IllegalStateException("Cannot reenroll a WAITLISTED enrollment");
    }

    @Override
    public boolean canDrop() {
        return true;
    }

    @Override
    public boolean canWithdraw() {
        return false;
    }

    @Override
    public boolean canComplete() {
        return false;
    }

    @Override
    public boolean canReenroll() {
        return false;
    }

    @Override
    public boolean isPending() {
        return false;
    }

    @Override
    public String getStatusName() {
        return "WAITLISTED";
    }

    @Override
    public void drop(EnrollmentContext ctx, String reason) {

    }
}
