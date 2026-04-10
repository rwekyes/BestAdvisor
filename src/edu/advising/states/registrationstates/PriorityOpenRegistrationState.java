package edu.advising.states.registrationstates;

import edu.advising.states.RegistrationState;

public class PriorityOpenRegistrationState implements RegistrationState {

    private static final PriorityOpenRegistrationState instance = new PriorityOpenRegistrationState();

    private PriorityOpenRegistrationState() {

    }

    public static PriorityOpenRegistrationState getInstance(){
        return instance;
    }
}
