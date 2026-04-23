package edu.advising.roster;

import edu.advising.commands.Section;
import edu.advising.users.Student;

import java.sql.SQLException;
import java.util.Iterator;
import java.util.List;

public class Roster implements Iterable {
    private Section section;
    private List<Student> students;

    public Roster(Section s) throws SQLException {
        this.section = s;
        this.students = section.getEnrolledStudents();
    }

    public RosterIterator createIterator(RosterSortOrder order){
        return null; // stub for now
    }

    public Section getSection(){
        return section;
    }

    public int size(){
        return students.size();
    }

    @Override
    public Iterator iterator() {
        return null;
    }
}
