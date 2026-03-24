package edu.advising.states.permissionstates;

import edu.advising.states.PermissionState;

public class NotRequested implements PermissionState {

    private static final NotRequested instance = new NotRequested();

    private NotRequested() {

    }

    public static NotRequested getInstance(){
        return instance;
    }
}
