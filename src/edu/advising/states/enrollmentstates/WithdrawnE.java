package edu.advising.states.enrollmentstates;

import edu.advising.states.EnrollmentState;

public class WithdrawnE implements EnrollmentState {

    private static final WithdrawnE instance = new WithdrawnE();

    private WithdrawnE() {

    }

    public static WithdrawnE getInstance(){
        return instance;
    }
}
