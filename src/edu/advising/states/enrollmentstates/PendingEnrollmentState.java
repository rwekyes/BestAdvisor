package edu.advising.states.enrollmentstates;

import edu.advising.contexts.EnrollmentContext;
import edu.advising.states.EnrollmentState;

public class PendingEnrollmentState implements EnrollmentState {

    private static final PendingEnrollmentState instance = new PendingEnrollmentState();

    private PendingEnrollmentState() {}

    public static PendingEnrollmentState getInstance() { return instance; }

    @Override
    public void confirm(EnrollmentContext ctx) {
        ctx.setState(EnrolledEnrollmentState.getInstance());
        ctx.getNotificationManager().notifyEnrollmentUpdate(ctx.getStudent(), ctx.getSection().getCourseCode(),"ENROLLED");
    }

    @Override
    public void drop(EnrollmentContext ctx) {
        throw new IllegalStateException("Cannot drop a PENDING enrollment");
    }

    @Override
    public void withdraw(EnrollmentContext ctx) {
        throw new IllegalStateException("Cannot withdraw a PENDING enrollment");
    }

    @Override
    public void complete(EnrollmentContext ctx, String finalGrade) {
        throw new IllegalStateException("Cannot complete a PENDING enrollment");
    }

    @Override
    public void reenroll(EnrollmentContext ctx) {
        throw new IllegalStateException("Cannot reenroll a PENDING enrollment");
    }

    @Override
    public boolean canDrop() {
        return false;
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
        return true;
    }

    @Override
    public String getStatusName() {
        return "PENDING";
    }

    @Override
    public void drop(EnrollmentContext ctx, String reason) {
        throw new IllegalStateException("Cannot drop a PENDING enrollment");
    }
}
