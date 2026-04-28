package edu.advising.schedule;

import edu.advising.model.Enrollment;
import edu.advising.model.Section;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class SemesterIterator implements ScheduleIterator {
    private static final Map<String, Integer> SEMESTER_RANK = Map.of(
            "SPRING", 1,
            "SUMMER", 2,
            "FALL",   3
    );

    private final List<Enrollment> sorted;
    private int cursor;

    public SemesterIterator(List<Enrollment> enrollments) {
        sorted = new ArrayList<>(enrollments);
        sorted.sort((a, b) -> {
            try {
                Section sA = a.getSection();
                Section sB = b.getSection();
                int yearCmp = Integer.compare(sB.getYear(), sA.getYear()); // most recent first
                if (yearCmp != 0) return yearCmp;
                int rankA = SEMESTER_RANK.getOrDefault(sA.getSemester().toUpperCase(), 0);
                int rankB = SEMESTER_RANK.getOrDefault(sB.getSemester().toUpperCase(), 0);
                return Integer.compare(rankB, rankA); // FALL before SPRING within same year
            } catch (SQLException e) {
                return 0;
            }
        });
        cursor = 0;
    }

    @Override public boolean hasNext() { return cursor < sorted.size(); }
    @Override public Enrollment next() { return sorted.get(cursor++); }
    @Override public void reset() { cursor = 0; }
}