package edu.advising.states;

import edu.advising.contexts.EnrollmentContext;

import java.sql.SQLException;

public interface EnrollmentState extends State {
    void confirm(EnrollmentContext ctx);
    void drop(EnrollmentContext ctx) throws SQLException;
    void withdraw(EnrollmentContext ctx) throws SQLException;
    void complete(EnrollmentContext ctx, String finalGrade);
    void reenroll(EnrollmentContext ctx);

    boolean canDrop();
    boolean canWithdraw();
    boolean canComplete();
    boolean canReenroll();
    boolean isPending();

    String getStatusName();

    void drop(EnrollmentContext ctx, String reason) throws SQLException;
}
