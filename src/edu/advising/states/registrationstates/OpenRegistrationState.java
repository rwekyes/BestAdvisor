package edu.advising.states.registrationstates;

import edu.advising.commands.RegistrationPeriod;
import edu.advising.contexts.RegistrationPeriodContext;
import edu.advising.states.RegistrationState;

import java.time.LocalDateTime;

public class OpenRegistrationState implements RegistrationState {

    private static final OpenRegistrationState instance = new OpenRegistrationState();

    private OpenRegistrationState() {

    }

    public static OpenRegistrationState getInstance(){
        return instance;
    }

    @Override
    public void open(RegistrationPeriodContext ctx) {
        throw new IllegalStateException("Cannot open an OPEN registration");
    }

    @Override
    public void transitionToLate(RegistrationPeriodContext ctx) {
        ctx.setState(LateRegistrationState.getInstance());
        ctx.persist(); // Admin call, needs its own persist
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
        if (now.isAfter(p.getCloseDate())) {
            ctx.setState(LateRegistrationState.getInstance());
            if (p.getLateRegistrationEnd() != null && now.isAfter(p.getLateRegistrationEnd())) {
                ctx.setState(ClosedRegistrationState.getInstance());
            }
        }
    }

    @Override
    public boolean canRegister(RegistrationPeriodContext ctx) {
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
        return "OPEN";
    }
}
