package edu.advising.states.permissionstates;

import edu.advising.states.PermissionState;

public class Granted implements PermissionState {

    private static final Granted instance = new Granted();

    private Granted() {

    }

    public static Granted getInstance(){
        return instance;
    }
}
