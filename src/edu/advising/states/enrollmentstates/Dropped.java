package edu.advising.states.enrollmentstates;

import edu.advising.states.EnrollmentState;

public class Dropped implements EnrollmentState {

    private static final Dropped instance = new Dropped();

    private Dropped() {

    }

    public static Dropped getInstance(){
        return instance;
    }
}
