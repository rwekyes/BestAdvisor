package edu.advising.states.enrollmentstates;

import edu.advising.contexts.EnrollmentContext;
import edu.advising.states.EnrollmentState;

public class CompletedEnrollmentState implements EnrollmentState {

    private static final CompletedEnrollmentState instance = new CompletedEnrollmentState();

    private CompletedEnrollmentState() {}

    public static CompletedEnrollmentState getInstance(){
        return instance;
    }

    @Override
    public void confirm(EnrollmentContext ctx) {
        throw new IllegalStateException("Cannot confirm a COMPLETED enrollment");
    }

    @Override
    public void drop(EnrollmentContext ctx) {
        throw new IllegalStateException("Cannot drop a COMPLETED enrollment");
    }

    @Override
    public void withdraw(EnrollmentContext ctx) {
        throw new IllegalStateException("Cannot withdraw a COMPLETED enrollment");
    }

    @Override
    public void complete(EnrollmentContext ctx, String finalGrade) {
        throw new IllegalStateException("Cannot complete a COMPLETED enrollment");
    }

    @Override
    public void reenroll(EnrollmentContext ctx) {
        throw new IllegalStateException("Cannot reenroll a COMPLETED enrollment");
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
        return false;
    }

    @Override
    public String getStatusName() {
        return "COMPLETED";
    }

    @Override
    public void drop(EnrollmentContext ctx, String reason) {
        throw new IllegalStateException("Cannot drop a COMPLETED enrollment");
    }
}
