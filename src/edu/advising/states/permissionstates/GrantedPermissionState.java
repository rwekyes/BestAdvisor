package edu.advising.states.permissionstates;

import edu.advising.states.PermissionState;

public class GrantedPermissionState implements PermissionState {

    private static final GrantedPermissionState instance = new GrantedPermissionState();

    private GrantedPermissionState() {

    }

    public static GrantedPermissionState getInstance(){
        return instance;
    }
}
