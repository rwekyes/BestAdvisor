package edu.advising.roster;

import edu.advising.users.Student;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class StudentIdRosterIterator implements RosterIterator{

    private List<Student> sorted;

    private int cursor;

    public StudentIdRosterIterator(List<Student> students){
        sorted = new ArrayList<Student>(students);
        sorted.sort(Comparator.comparing(Student::getStudentId));
        cursor = 0;
    }

    @Override
    public boolean hasNext() {
        return cursor < sorted.size();
    }

    @Override
    public Student next() {
        if (!hasNext()) {
            System.err.println("Student Roster has no more elements");
        }
        return sorted.get(cursor++);
    }

    @Override
    public void reset() {
        cursor = 0;
    }
}
