package edu.advising.states.facultypermissionstates;

import edu.advising.contexts.FacultyPermissionContext;
import edu.advising.states.FacultyPermissionState;

import java.sql.SQLException;
import java.time.LocalDateTime;

public class ApprovedPermissionState implements FacultyPermissionState {

    private static final ApprovedPermissionState instance = new ApprovedPermissionState();

    private ApprovedPermissionState() {

    }

    public static ApprovedPermissionState getInstance(){
        return instance;
    }

    @Override
    public void approve(FacultyPermissionContext ctx) {
        throw new IllegalStateException("Cannot approve an APPROVED faculty permission");
    }

    @Override
    public void deny(FacultyPermissionContext ctx) {
        revoke(ctx);
    }

    @Override
    public void expire(FacultyPermissionContext ctx) {
        ctx.setState(ExpiredPermissionState.getInstance());
    }

    @Override
    public void resubmit(FacultyPermissionContext ctx) {
        throw new IllegalStateException("Cannot resubmit an APPROVED faculty permission");
    }

    @Override
    public void revoke(FacultyPermissionContext ctx) {
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
    public boolean isValid(FacultyPermissionContext ctx) {
        return true;
    }

    @Override
    public String getStatusName() {
        return "APPROVED";
    }

    @Override
    public void checkAndAdvance(FacultyPermissionContext ctx) {
        LocalDateTime expiry = ctx.getFacultyPermission().getExpiryDate();
        if (expiry != null && LocalDateTime.now().isAfter(expiry)) {
            ctx.setState(ExpiredPermissionState.getInstance());
        }
    }
}
