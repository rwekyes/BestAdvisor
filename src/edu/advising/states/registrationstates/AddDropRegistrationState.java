package edu.advising.states.registrationstates;

import edu.advising.states.RegistrationState;

public class AddDropRegistrationState implements RegistrationState {

    private static final AddDropRegistrationState instance = new AddDropRegistrationState();

    private AddDropRegistrationState() {

    }

    public static AddDropRegistrationState getInstance(){
        return instance;
    }
}
