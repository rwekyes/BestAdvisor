package edu.advising.gradebook;

import edu.advising.model.Enrollment;
import java.util.List;
import java.util.stream.Collectors;

public class MissingGradeIterator implements GradebookIterator{
    private final List<Enrollment> filtered;
    private int cursor;

    private MissingGradeIterator(List<Enrollment> filtered) {
        this.filtered = filtered;
        this.cursor = 0;
    }

    public static MissingGradeIterator of(List<Enrollment> enrollments) {
        return new MissingGradeIterator(
                enrollments.stream()
                        .filter(e -> e.getFinalGrade() == null || e.getFinalGrade().isBlank())
                        .collect(Collectors.toList())
        );
    }

    @Override
    public boolean hasNext() {
        return cursor < filtered.size();
    }

    @Override
    public Enrollment next() {
        return filtered.get(cursor++);
    }

    @Override
    public void reset() {
        cursor = 0;
    }
}
