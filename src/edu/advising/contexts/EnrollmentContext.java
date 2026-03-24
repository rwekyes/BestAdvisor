package edu.advising.contexts;

import edu.advising.states.EnrollmentState;
import edu.advising.states.StateFactory;

public class EnrollmentContext {

    private EnrollmentState state;

    public static EnrollmentContext load(String s) {
        EnrollmentContext context = new EnrollmentContext();
        context.state = (EnrollmentState) StateFactory.enrollmentStateFor(s);
        return context;
    }

    public void updateState(EnrollmentState s) {
        this.state = s;
    }

    public EnrollmentState getState() {
        return state;
    }

}
