package edu.advising.states.permissionstates;

import edu.advising.states.PermissionState;

public class Denied implements PermissionState {

    private static final Denied instance = new Denied();

    private Denied() {

    }

    public static Denied getInstance(){
        return instance;
    }
}
