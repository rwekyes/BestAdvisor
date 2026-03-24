package edu.advising.states.enrollmentstates;

import edu.advising.states.EnrollmentState;

public class Enrolled implements EnrollmentState {

    private static final Enrolled instance = new Enrolled();

    private Enrolled() {

    }

    public static Enrolled getInstance(){
        return instance;
    }
}
