package edu.advising.contexts;

import edu.advising.states.RegistrationState;
import edu.advising.states.StateFactory;

public class RegistrationContext {

    private RegistrationState state;

    public RegistrationContext load(String s){
        RegistrationContext context = new RegistrationContext();
        context.state = (RegistrationState) StateFactory.registrationStateFor(s);
        return context;
    }

    public void updateState(RegistrationState s) {
        this.state = s;
    }

    public RegistrationState getState() {
        return state;
    }
}
