package edu.advising.states.registrationstates;

import edu.advising.commands.RegistrationPeriod;
import edu.advising.contexts.RegistrationPeriodContext;
import edu.advising.states.RegistrationState;

import java.time.LocalDateTime;

public class LateRegistrationState implements RegistrationState {

    private static final LateRegistrationState instance = new LateRegistrationState();

    private LateRegistrationState() {

    }

    public static LateRegistrationState getInstance(){
        return instance;
    }

    @Override
    public void open(RegistrationPeriodContext ctx) {
        throw new IllegalStateException("Cannot open a LATE registration");
    }

    @Override
    public void transitionToLate(RegistrationPeriodContext ctx) {
        throw new IllegalStateException("Cannot transition to late a LATE registration");
    }

    @Override
    public void close(RegistrationPeriodContext ctx) {
        ctx.setState(ClosedRegistrationState.getInstance());
        ctx.persist(); // Admin call, needs its own persist
    }

    @Override
    public void checkAndAdvance(RegistrationPeriodContext ctx) {
        LocalDateTime now = LocalDateTime.now();
        RegistrationPeriod p = ctx.getRegistrationPeriod();
        if (p.getLateRegistrationEnd() != null && now.isAfter(p.getLateRegistrationEnd())) {
            ctx.setState(ClosedRegistrationState.getInstance());
        }
    }

    @Override
    public boolean canRegister(RegistrationPeriodContext ctx) {
        System.out.println("Warning - This registration may result in late fees");
        return true;
    }

    @Override
    public boolean canDrop(RegistrationPeriodContext ctx) {
        return true;
    }

    @Override
    public boolean isOpen(RegistrationPeriodContext ctx) {
        return true;
    }

    @Override
    public String getStatusName() {
        return "LATE";
    }
}
