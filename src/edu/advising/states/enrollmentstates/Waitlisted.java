package edu.advising.states.enrollmentstates;

import edu.advising.states.EnrollmentState;

public class Waitlisted implements EnrollmentState {

    private static final Waitlisted instance = new Waitlisted();

    private Waitlisted() {

    }

    public static Waitlisted getInstance(){
        return instance;
    }
}
