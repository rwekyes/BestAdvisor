package edu.advising.states;

import edu.advising.contexts.RegistrationPeriodContext;

public interface RegistrationState extends State {
    void open(RegistrationPeriodContext ctx);
    void transitionToLate(RegistrationPeriodContext ctx);
    void close(RegistrationPeriodContext ctx);
    void checkAndAdvance(RegistrationPeriodContext ctx);
    boolean canRegister(RegistrationPeriodContext ctx);
    boolean canDrop(RegistrationPeriodContext ctx);
    boolean isOpen(RegistrationPeriodContext ctx);
    String getStatusName();
}
