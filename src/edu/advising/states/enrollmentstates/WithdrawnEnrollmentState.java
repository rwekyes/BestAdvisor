package edu.advising.states.enrollmentstates;

import edu.advising.contexts.EnrollmentContext;
import edu.advising.states.EnrollmentState;

public class WithdrawnEnrollmentState implements EnrollmentState {

    private static final WithdrawnEnrollmentState instance = new WithdrawnEnrollmentState();

    private WithdrawnEnrollmentState() {}

    public static WithdrawnEnrollmentState getInstance(){
        return instance;
    }

    @Override
    public void confirm(EnrollmentContext ctx) {
        throw new IllegalStateException("Cannot confirm a WITHDRAWN enrollment");
    }

    @Override
    public void drop(EnrollmentContext ctx) {
        throw new IllegalStateException("Cannot drop a WITHDRAWN enrollment");
    }

    @Override
    public void withdraw(EnrollmentContext ctx) {
        throw new IllegalStateException("Cannot withdraw a WITHDRAWN enrollment");
    }

    @Override
    public void complete(EnrollmentContext ctx, String finalGrade) {
        throw new IllegalStateException("Cannot complete a WITHDRAWN enrollment");
    }

    @Override
    public void reenroll(EnrollmentContext ctx) {
        throw new IllegalStateException("Cannot reenroll a WITHDRAWN enrollment");
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
        return "WITHDRAWN";
    }

    @Override
    public void drop(EnrollmentContext ctx, String reason) {
        throw new IllegalStateException("Cannot drop a WITHDRAWN enrollment");
    }
}
