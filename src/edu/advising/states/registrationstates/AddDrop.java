package edu.advising.states.registrationstates;

import edu.advising.states.RegistrationState;

public class AddDrop implements RegistrationState {

    private static final AddDrop instance = new AddDrop();

    private AddDrop() {

    }

    public static AddDrop getInstance(){
        return instance;
    }
}
