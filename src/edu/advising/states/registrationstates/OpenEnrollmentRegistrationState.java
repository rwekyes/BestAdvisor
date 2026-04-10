package edu.advising.states.registrationstates;

import edu.advising.states.RegistrationState;

public class OpenEnrollmentRegistrationState implements RegistrationState {

    private static final OpenEnrollmentRegistrationState instance = new OpenEnrollmentRegistrationState();

    private OpenEnrollmentRegistrationState() {

    }

    public static OpenEnrollmentRegistrationState getInstance(){
        return instance;
    }
}
