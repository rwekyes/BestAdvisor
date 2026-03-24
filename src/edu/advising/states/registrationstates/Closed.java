package edu.advising.states.registrationstates;

import edu.advising.states.RegistrationState;

public class Closed implements RegistrationState {

    private static final Closed instance = new Closed();

    private Closed() {

    }

    public static Closed getInstance(){
        return instance;
    }
}
