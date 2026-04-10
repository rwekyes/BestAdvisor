package edu.advising.states.enrollmentstates;

import edu.advising.contexts.EnrollmentContext;
import edu.advising.states.EnrollmentState;

public class DroppedEnrollmentState implements EnrollmentState {

    private static final DroppedEnrollmentState instance = new DroppedEnrollmentState();

    private DroppedEnrollmentState() {}

    public static DroppedEnrollmentState getInstance(){
        return instance;
    }

    @Override
    public void confirm(EnrollmentContext ctx) {
        throw new IllegalStateException("Cannot confirm a DROPPED enrollment");
    }

    @Override
    public void drop(EnrollmentContext ctx) {
        throw new IllegalStateException("Cannot drop a DROPPED enrollment");
    }

    @Override
    public void withdraw(EnrollmentContext ctx) {
        throw new IllegalStateException("Cannot withdraw a DROPPED enrollment");
    }

    @Override
    public void complete(EnrollmentContext ctx, String finalGrade) {
        throw new IllegalStateException("Cannot complete a DROPPED enrollment");
    }

    @Override
    public void reenroll(EnrollmentContext ctx) {
        if(!ctx.getSection().hasCapacity()){
            System.out.println("Class is at capacity!");
            // TODO: trigger the logic to ask if they want to be added to the waitlist
            return;
        }
        ctx.setState(EnrolledEnrollmentState.getInstance());
        ctx.getNotificationManager().notifyEnrollmentUpdate(ctx.getStudent(), ctx.getSection().getCourseCode(),"ENROLLED");
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
        return true;
    }

    @Override
    public boolean isPending() {
        return false;
    }

    @Override
    public String getStatusName() {
        return "DROPPED";
    }

    @Override
    public void drop(EnrollmentContext ctx, String reason) {
        throw new IllegalStateException("Cannot drop a DROPPED enrollment");
    }
}
