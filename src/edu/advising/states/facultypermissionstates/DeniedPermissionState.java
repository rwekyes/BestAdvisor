package edu.advising.states.facultypermissionstates;

import edu.advising.contexts.FacultyPermissionContext;
import edu.advising.states.FacultyPermissionState;

import java.time.LocalDateTime;

public class DeniedPermissionState implements FacultyPermissionState {

    private static final DeniedPermissionState instance = new DeniedPermissionState();

    private DeniedPermissionState() {

    }

    public static DeniedPermissionState getInstance(){
        return instance;
    }

    @Override
    public void approve(FacultyPermissionContext ctx) {
        throw new IllegalStateException("Cannot approve a DENIED faculty permission");
    }

    @Override
    public void deny(FacultyPermissionContext ctx) {
        throw new IllegalStateException("Cannot deny a DENIED faculty permission");
    }

    @Override
    public void expire(FacultyPermissionContext ctx) {
        throw new IllegalStateException("Cannot expire a DENIED faculty permission");
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
        throw new IllegalStateException("Cannot revoke a DENIED faculty permission");
    }

    @Override
    public boolean isValid(FacultyPermissionContext ctx) {
        return false;
    }

    @Override
    public String getStatusName() {
        return "DENIED";
    }

    @Override
    public void checkAndAdvance(FacultyPermissionContext ctx) {

    }
}
