package edu.advising.states.registrationstates;

import edu.advising.states.RegistrationState;

public class ClosedRegistrationState implements RegistrationState {

    private static final ClosedRegistrationState instance = new ClosedRegistrationState();

    private ClosedRegistrationState() {

    }

    public static ClosedRegistrationState getInstance(){
        return instance;
    }
}
