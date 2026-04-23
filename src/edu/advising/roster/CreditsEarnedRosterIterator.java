package edu.advising.roster;

import edu.advising.users.Student;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class CreditsEarnedRosterIterator implements RosterIterator{

    private List<Student> sorted;

    private int cursor;

    public CreditsEarnedRosterIterator(List<Student> students){
        sorted = new ArrayList<Student>(students);
        sorted.sort(Comparator.comparing(Student::getGpa)); //TODO: Set up a credits field in both the db and Student object
        cursor = 0;
    }

    @Override
    public boolean hasNext() {
        return false;
    }

    @Override
    public Student next() {
        return null;
    }

    @Override
    public void reset() {

    }
}
