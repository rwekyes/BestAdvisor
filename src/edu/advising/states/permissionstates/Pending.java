package edu.advising.states.permissionstates;

import edu.advising.states.PermissionState;

public class Pending implements PermissionState {

    private static final Pending instance = new Pending();

    private Pending() {

    }

    public static Pending getInstance(){
        return instance;
    }
}
