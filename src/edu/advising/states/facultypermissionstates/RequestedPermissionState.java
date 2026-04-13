package edu.advising.states.facultypermissionstates;

import edu.advising.contexts.FacultyPermissionContext;
import edu.advising.states.FacultyPermissionState;

import java.sql.SQLException;

public class RequestedPermissionState implements FacultyPermissionState {

    private static final RequestedPermissionState instance = new RequestedPermissionState();

    private RequestedPermissionState() {

    }

    public static RequestedPermissionState getInstance(){
        return instance;
    }

    @Override
    public void approve(FacultyPermissionContext ctx) {
        ctx.setState(ApprovedPermissionState.getInstance());
        if (ctx.getWaitlistEntry() != null) {
            try {
                ctx.getNotificationManager().notifyPermissionDecision(
                        ctx.getWaitlistEntry().getStudent(), ctx.getSection().getCourseCode(), "APPROVED");
            } catch (SQLException e) {
                System.err.println("Failed to notify - SQL Exception - " + e);
            }
        }
    }

    @Override
    public void deny(FacultyPermissionContext ctx) {
        ctx.setState(DeniedPermissionState.getInstance());
        if (ctx.getWaitlistEntry() != null) {
            try {
                ctx.getNotificationManager().notifyPermissionDecision(
                        ctx.getWaitlistEntry().getStudent(), ctx.getSection().getCourseCode(), "DENIED");
            } catch (SQLException e) {
                System.err.println("Failed to notify - SQL Exception - " + e);
            }
        }
    }

    @Override
    public void expire(FacultyPermissionContext ctx) {
        throw new IllegalStateException("Cannot expire a REQUESTED faculty permission");
    }

    @Override
    public void resubmit(FacultyPermissionContext ctx) {
        throw new IllegalStateException("Cannot resubmit a REQUESTED faculty permission");
    }

    @Override
    public void revoke(FacultyPermissionContext ctx) {
        throw new IllegalStateException("Cannot revoke a REQUESTED faculty permission");
    }

    @Override
    public boolean isValid(FacultyPermissionContext ctx) {
        return false;
    }

    @Override
    public String getStatusName() {
        return "REQUESTED";
    }

    @Override
    public void checkAndAdvance(FacultyPermissionContext ctx) {

    }
}
