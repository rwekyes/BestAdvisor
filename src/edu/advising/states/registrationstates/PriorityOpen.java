package edu.advising.states.registrationstates;

import edu.advising.states.RegistrationState;

public class PriorityOpen implements RegistrationState {

    private static final PriorityOpen instance = new PriorityOpen();

    private PriorityOpen() {

    }

    public static PriorityOpen getInstance(){
        return instance;
    }
}
