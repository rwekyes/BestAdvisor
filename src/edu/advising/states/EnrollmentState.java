package edu.advising.states;

import edu.advising.contexts.EnrollmentContext;

public interface EnrollmentState extends State {
    void confirm(EnrollmentContext ctx);
    void drop(EnrollmentContext ctx);
    void withdraw(EnrollmentContext ctx);
    void complete(EnrollmentContext ctx, String finalGrade);
    void reenroll(EnrollmentContext ctx);

    boolean canDrop();
    boolean canWithdraw();
    boolean canComplete();
    boolean canReenroll();
    boolean isPending();

    String getStatusName();

    void drop(EnrollmentContext ctx, String reason);
}
