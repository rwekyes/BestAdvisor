package edu.advising.states.facultypermissionstates;

import edu.advising.contexts.FacultyPermissionContext;
import edu.advising.states.FacultyPermissionState;

import java.time.LocalDateTime;

public class ExpiredPermissionState implements FacultyPermissionState {

    private static final ExpiredPermissionState instance = new ExpiredPermissionState();

    private ExpiredPermissionState() {

    }

    public static ExpiredPermissionState getInstance(){
        return instance;
    }

    @Override
    public void approve(FacultyPermissionContext ctx) {
        throw new IllegalStateException("Cannot approve an EXPIRED faculty permission");
    }

    @Override
    public void deny(FacultyPermissionContext ctx) {
        throw new IllegalStateException("Cannot deny an EXPIRED faculty permission");
    }

    @Override
    public void expire(FacultyPermissionContext ctx) {
        throw new IllegalStateException("Cannot expire an EXPIRED faculty permission");
    }

    @Override
    public void resubmit(FacultyPermissionContext ctx) {
        ctx.getFacultyPermission().setRequestDate(LocalDateTime.now());
        ctx.getFacultyPermission().setExpiryDate(LocalDateTime.now().plusHours(48));
        ctx.getFacultyPermission().setDenyReason(null);
        ctx.setState(RequestedPermissionState.getInstance());
        ctx.getNotificationManager().notifyPermissionRequest(
                ctx.getFaculty(), ctx.getSection().getCourseCode());
    }

    @Override
    public void revoke(FacultyPermissionContext ctx) {
        throw new IllegalStateException("Cannot revoke an EXPIRED faculty permission");
    }

    @Override
    public boolean isValid(FacultyPermissionContext ctx) {
        return false;
    }

    @Override
    public String getStatusName() {
        return "EXPIRED";
    }

    @Override
    public void checkAndAdvance(FacultyPermissionContext ctx) {

    }
}
