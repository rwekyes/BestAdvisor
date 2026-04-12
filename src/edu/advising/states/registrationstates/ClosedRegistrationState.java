package edu.advising.states.registrationstates;

import edu.advising.contexts.RegistrationPeriodContext;
import edu.advising.states.RegistrationState;

public class ClosedRegistrationState implements RegistrationState {

    private static final ClosedRegistrationState instance = new ClosedRegistrationState();

    private ClosedRegistrationState() {

    }

    public static ClosedRegistrationState getInstance(){
        return instance;
    }

    @Override
    public void open(RegistrationPeriodContext ctx) {
        throw new IllegalStateException("Cannot open a CLOSED registration");
    }

    @Override
    public void transitionToLate(RegistrationPeriodContext ctx) {
        throw new IllegalStateException("Cannot transition to late a CLOSED registration");
    }

    @Override
    public void close(RegistrationPeriodContext ctx) {
        throw new IllegalStateException("Cannot close a CLOSED registration");
    }

    @Override
    public void checkAndAdvance(RegistrationPeriodContext ctx) {
        // Does nothing so if it gets hit no one is the wiser
    }

    @Override
    public boolean canRegister(RegistrationPeriodContext ctx) {
        System.out.println("Cannot register after registration closes");
        return false;
    }

    @Override
    public boolean canDrop(RegistrationPeriodContext ctx) {
        System.out.println("Cannot drop after registration closes");
        return false;
    }

    @Override
    public boolean isOpen(RegistrationPeriodContext ctx) {
        return false;
    }

    @Override
    public String getStatusName() {
        return "CLOSED";
    }
}
