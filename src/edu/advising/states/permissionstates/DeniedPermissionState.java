package edu.advising.states.permissionstates;

import edu.advising.states.PermissionState;

public class DeniedPermissionState implements PermissionState {

    private static final DeniedPermissionState instance = new DeniedPermissionState();

    private DeniedPermissionState() {

    }

    public static DeniedPermissionState getInstance(){
        return instance;
    }
}
