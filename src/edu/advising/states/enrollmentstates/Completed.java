package edu.advising.states.enrollmentstates;

import edu.advising.states.EnrollmentState;

public class Completed implements EnrollmentState {

    private static final Completed instance = new Completed();

    private Completed() {

    }

    public static Completed getInstance(){
        return instance;
    }
}
