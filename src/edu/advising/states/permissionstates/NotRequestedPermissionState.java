package edu.advising.states.permissionstates;

import edu.advising.states.PermissionState;

public class NotRequestedPermissionState implements PermissionState {

    private static final NotRequestedPermissionState instance = new NotRequestedPermissionState();

    private NotRequestedPermissionState() {

    }

    public static NotRequestedPermissionState getInstance(){
        return instance;
    }
}
