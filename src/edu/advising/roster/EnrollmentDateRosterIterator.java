package edu.advising.roster;

import edu.advising.model.Enrollment;
import edu.advising.model.Section;
import edu.advising.users.Student;

import java.sql.SQLException;
import java.util.*;
import java.util.stream.Collectors;

public class EnrollmentDateRosterIterator implements RosterIterator{

    private List<Student> sorted;

    private int cursor;

    public EnrollmentDateRosterIterator(Section section) throws SQLException {

        Map<String, Student> studentMap = section.getEnrolledStudents().stream()
                .collect(Collectors.toMap(Student::getStudentId, s -> s));

        sorted = section.getEnrollments().stream()
                .sorted(Comparator.comparing(Enrollment::getEnrollmentDate))
                .map(e -> studentMap.get(e.getStudentId()))
                .filter(Objects::nonNull) // safety in case of mismatches
                .toList();

        cursor = 0;
    }

    @Override
    public boolean hasNext() {
        return cursor < sorted.size();
    }

    @Override
    public Student next() {
        return sorted.get(cursor++);
    }

    @Override
    public void reset() {
        cursor = 0;
    }
}
