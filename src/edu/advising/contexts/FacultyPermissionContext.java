package edu.advising.contexts;

import edu.advising.model.FacultyPermission;
import edu.advising.model.Section;
import edu.advising.model.WaitlistEntry;
import edu.advising.core.DatabaseManager;
import edu.advising.notifications.NotificationManager;
import edu.advising.states.FacultyPermissionState;
import edu.advising.states.StateFactory;
import edu.advising.states.facultypermissionstates.*;
import edu.advising.users.Faculty;

import java.sql.SQLException;
import java.time.LocalDateTime;

public class FacultyPermissionContext {

    private FacultyPermission facultyPermission;
    private FacultyPermissionState state;
    private NotificationManager notificationManager;
    private Faculty faculty;
    private Section section;
    private WaitlistEntry waitlistEntry;

    private FacultyPermissionContext(){}

    private FacultyPermissionContext(FacultyPermission facultyPermission, Faculty faculty){
        this.facultyPermission = facultyPermission;
        this.faculty = faculty;
    }

    public static FacultyPermissionContext create(WaitlistEntry entry, Section section,
                                                  Faculty faculty) throws SQLException, IllegalAccessException {
        FacultyPermission fp = new FacultyPermission();
        fp.setSectionId(section.getId());
        fp.setWaitlistId(entry.getId());
        fp.setRequestDate(LocalDateTime.now());
        fp.setExpiryDate(LocalDateTime.now().plusHours(48));
        fp.setStatus("REQUESTED");
        DatabaseManager.getInstance().upsert(fp);

        FacultyPermissionContext ctx = new FacultyPermissionContext(fp, faculty);
        ctx.state = StateFactory.facultyPermissionStateFor("REQUESTED");
        ctx.notificationManager = NotificationManager.getInstance();
        ctx.section = section;
        ctx.waitlistEntry = entry;
        ctx.notificationManager.notifyPermissionRequest(faculty, section.getCourseCode());
        return ctx;
    }

    public static FacultyPermissionContext load(FacultyPermission fp, Faculty faculty,
                                                Section section, WaitlistEntry entry) {
        FacultyPermissionContext ctx = new FacultyPermissionContext(fp, faculty);
        ctx.state = StateFactory.facultyPermissionStateFor(fp.getStatus());
        ctx.notificationManager = NotificationManager.getInstance();
        ctx.section = section;
        ctx.waitlistEntry = entry;
        // Auto-expire on load per acceptance criteria
        if (fp.getExpiryDate() != null && LocalDateTime.now().isAfter(fp.getExpiryDate())
                && ctx.state instanceof ApprovedPermissionState) {
            ctx.getFacultyPermission().setStatus("EXPIRED");
            ctx.state = ExpiredPermissionState.getInstance();
            ctx.persist();
            // Only notify if we have enough context to do so
            if (ctx.waitlistEntry != null && ctx.faculty != null) {
                try {
                    ctx.notificationManager.notifyPermissionDecision(entry.getStudent(),
                            section.getCourseCode(), "EXPIRED");
                } catch (SQLException e) {
                    System.err.println("Failed to notify - SQL Exception - " + e);
                }
            }
        }
        return ctx;
    }

    public void persist(){
        if (facultyPermission.getId() == 0) return;  // in-memory only, no DB row to update
        try {
            DatabaseManager.getInstance().upsert(facultyPermission);
        } catch (SQLException | IllegalAccessException e) {
            throw new RuntimeException("Failed to persist faculty permission state - ", e);
        }
    }

    public void checkAndAdvance() {
        state.checkAndAdvance(this);
        persist();
    }

    public void approve(){
        state.approve(this);
        persist();
    }
    public void deny(){
        state.deny(this);
        persist();
    }
    public void expire(){
        state.expire(this);
        persist();
    }
    public void resubmit(){
        state.resubmit(this);
        persist();
    }
    public void revoke(){
        state.revoke(this);
        persist();
    }
    public boolean isValid(){return state.isValid(this);}

    public void setState(FacultyPermissionState newState) {
        this.state = newState;
        this.facultyPermission.setStatus(newState.getStatusName());
    }

    public FacultyPermissionState getState() {
        return state;
    }

    public NotificationManager getNotificationManager() {
        return notificationManager;
    }

    public FacultyPermission getFacultyPermission(){return facultyPermission;}
    public Faculty getFaculty(){return faculty;}
    public Section getSection(){return section;}

    public WaitlistEntry getWaitlistEntry() {
        return waitlistEntry;
    }
}
