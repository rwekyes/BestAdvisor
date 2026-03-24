package edu.advising.contexts;

import edu.advising.states.PermissionState;
import edu.advising.states.StateFactory;

public class PermissionContext {

    private PermissionState state;

    public static PermissionContext load(String s){
        PermissionContext context = new PermissionContext();
        context.state = (PermissionState) StateFactory.permissionStateFor(s);
        return context;
    }

    public void updateState(PermissionState s) {
        this.state = s;
    }

    public PermissionState getState() {
        return state;
    }
}
