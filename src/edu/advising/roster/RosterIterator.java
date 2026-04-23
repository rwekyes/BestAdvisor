package edu.advising.roster;

import edu.advising.users.Student;

public interface RosterIterator {
    public boolean hasNext();
    public Student next();
    public void reset();
}
