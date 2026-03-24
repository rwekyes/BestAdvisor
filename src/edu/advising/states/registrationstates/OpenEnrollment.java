package edu.advising.states.registrationstates;

import edu.advising.states.RegistrationState;

public class OpenEnrollment implements RegistrationState {

    private static final OpenEnrollment instance = new OpenEnrollment();

    private OpenEnrollment() {

    }

    public static OpenEnrollment getInstance(){
        return instance;
    }
}
