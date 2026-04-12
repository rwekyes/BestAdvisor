package edu.advising.states.registrationstates;

import edu.advising.commands.RegistrationPeriod;
import edu.advising.contexts.RegistrationPeriodContext;
import edu.advising.states.RegistrationState;

import java.time.LocalDateTime;

public class NotOpenRegistrationState implements RegistrationState {

    private static final NotOpenRegistrationState instance = new NotOpenRegistrationState();

    private NotOpenRegistrationState() {

    }

    public static NotOpenRegistrationState getInstance(){
        return instance;
    }

    @Override
    public void open(RegistrationPeriodContext ctx) {
        ctx.setState(OpenRegistrationState.getInstance());
        ctx.persist(); // Admin call, needs its own persist
    }

    @Override
    public void transitionToLate(RegistrationPeriodContext ctx) {
        throw new IllegalStateException("Cannot transition to late a NOT_OPEN registration");
    }

    @Override
    public void close(RegistrationPeriodContext ctx) {
        throw new IllegalStateException("Cannot close a NOT_OPEN registration");
    }

    @Override
    public void checkAndAdvance(RegistrationPeriodContext ctx) {
        LocalDateTime now = LocalDateTime.now();
        RegistrationPeriod p = ctx.getRegistrationPeriod();
        if (now.isAfter(p.getOpenDate())) {
            ctx.setState(OpenRegistrationState.getInstance());
            // also check if we've already passed closeDate
            if (now.isAfter(p.getCloseDate())) {
                ctx.setState(LateRegistrationState.getInstance());
                if (p.getLateRegistrationEnd() != null && now.isAfter(p.getLateRegistrationEnd())) {
                    ctx.setState(ClosedRegistrationState.getInstance());
                }
            }
        }
    }

    @Override
    public boolean canRegister(RegistrationPeriodContext ctx) {
        System.out.println("Cannot register before registration opens");
        return false;
    }

    @Override
    public boolean canDrop(RegistrationPeriodContext ctx) {
        System.out.println("Cannot drop before registration opens");
        return false;
    }

    @Override
    public boolean isOpen(RegistrationPeriodContext ctx) {
        return false;
    }

    @Override
    public String getStatusName() {
        return "NOT_OPEN";
    }
}
