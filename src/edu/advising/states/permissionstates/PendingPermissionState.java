package edu.advising.states.permissionstates;

import edu.advising.states.PermissionState;

public class PendingPermissionState implements PermissionState {

    private static final PendingPermissionState instance = new PendingPermissionState();

    private PendingPermissionState() {

    }

    public static PendingPermissionState getInstance(){
        return instance;
    }
}
