package edu.advising.gradebook;

import edu.advising.model.Enrollment;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;

public class ByGradeIterator implements GradebookIterator{

    private final List<Enrollment> sorted;
    private int cursor;

    private static final Map<String, Integer> GRADE_RANK = Map.ofEntries(
            Map.entry("A",  0), Map.entry("A-", 1),
            Map.entry("B+", 2), Map.entry("B",  3), Map.entry("B-", 4),
            Map.entry("C+", 5), Map.entry("C",  6), Map.entry("C-", 7),
            Map.entry("D+", 8), Map.entry("D",  9), Map.entry("D-", 10),
            Map.entry("F",  11), Map.entry("W", 12), Map.entry("I", 13),
            Map.entry("P",  14), Map.entry("NP", 15)
    );

    private ByGradeIterator(List<Enrollment> sorted) {
        this.sorted = sorted;
        this.cursor = 0;
    }

    public static ByGradeIterator descending(List<Enrollment> enrollments){
        List<Enrollment> desc = new ArrayList<Enrollment>(enrollments);
        desc.sort(Comparator.comparingInt(e -> GRADE_RANK.getOrDefault(e.getFinalGrade(), 99)));
        return new ByGradeIterator(desc);
    }

    public static ByGradeIterator ascending(List<Enrollment> enrollments){
        List<Enrollment> asc = new ArrayList<Enrollment>(enrollments);
        asc.sort(Comparator.comparingInt((Enrollment e) -> GRADE_RANK.getOrDefault(e.getFinalGrade(), 99)).reversed());
        return new ByGradeIterator(asc);
    }

    @Override
    public boolean hasNext() {
        return cursor < sorted.size();
    }

    @Override
    public Enrollment next() {
        return sorted.get(cursor++);
    }

    @Override
    public void reset() {
        cursor = 0;
    }
}
