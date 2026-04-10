package edu.advising.states.enrollmentstates;

import edu.advising.contexts.EnrollmentContext;
import edu.advising.states.EnrollmentState;

import java.time.LocalDateTime;

public class EnrolledEnrollmentState implements EnrollmentState {

    private static final EnrolledEnrollmentState instance = new EnrolledEnrollmentState();

    private EnrolledEnrollmentState() {}

    public static EnrolledEnrollmentState getInstance(){
        return instance;
    }

    @Override
    public void confirm(EnrollmentContext ctx) {
        throw new IllegalStateException("Cannot confirm an ENROLLED enrollment");
    }

    @Override
    public void drop(EnrollmentContext ctx) {
        ctx.setState(DroppedEnrollmentState.getInstance());
        ctx.getNotificationManager().notifyEnrollmentUpdate(ctx.getStudent(), ctx.getSection().getCourseCode(),"DROPPED");
    }

    @Override
    public void withdraw(EnrollmentContext ctx) {
        ctx.setState(WithdrawnEnrollmentState.getInstance());
        ctx.getNotificationManager().notifyEnrollmentUpdate(ctx.getStudent(), ctx.getSection().getCourseCode(),"WITHDRAWN");
    }

    @Override
    public void complete(EnrollmentContext ctx, String finalGrade) {
        ctx.getEnrollment().setFinalGrade(finalGrade);
        ctx.getEnrollment().setGradedAt(LocalDateTime.now());
        ctx.setState(CompletedEnrollmentState.getInstance());
        ctx.getNotificationManager().notifyEnrollmentUpdate(ctx.getStudent(), ctx.getSection().getCourseCode(),"COMPLETED");
    }

    @Override
    public void reenroll(EnrollmentContext ctx) {
        throw new IllegalStateException("Cannot reenroll an ENROLLED enrollment");
    }

    @Override
    public boolean canDrop() {
        return true;
    }

    @Override
    public boolean canWithdraw() {
        return true;
    }

    @Override
    public boolean canComplete() {
        return true;
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
        return "ENROLLED";
    }

    @Override
    public void drop(EnrollmentContext ctx, String reason) {
        ctx.getEnrollment().setDropReason(reason);
        ctx.getEnrollment().setDroppedAt(LocalDateTime.now());
        ctx.setState(DroppedEnrollmentState.getInstance());
        ctx.getNotificationManager().notifyEnrollmentUpdate(ctx.getStudent(), ctx.getSection().getCourseCode(),"DROPPED");
    }
}
