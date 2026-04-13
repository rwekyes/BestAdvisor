package edu.advising.states;

import edu.advising.contexts.FacultyPermissionContext;

public interface FacultyPermissionState extends State {
        void approve(FacultyPermissionContext ctx);
        void deny(FacultyPermissionContext ctx);
        void expire(FacultyPermissionContext ctx);
        void resubmit(FacultyPermissionContext ctx);
        void revoke(FacultyPermissionContext ctx);
        boolean isValid(FacultyPermissionContext ctx);
        String getStatusName();
        void checkAndAdvance(FacultyPermissionContext ctx);

}
