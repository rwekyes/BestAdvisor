package edu.advising.schedule;

import edu.advising.model.Enrollment;
import edu.advising.model.Section;

import java.sql.SQLException;
import java.time.DayOfWeek;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class WeeklyScheduleIterator implements ScheduleIterator {
    private final List<Enrollment> sorted;
    private int cursor;

    public WeeklyScheduleIterator(List<Enrollment> enrollments) {
        sorted = new ArrayList<>(enrollments);
        sorted.sort((a, b) -> {
            try {
                DayOfWeek dayA = earliestDay(a.getSection());
                DayOfWeek dayB = earliestDay(b.getSection());
                int dayCmp = dayA.compareTo(dayB);
                if (dayCmp != 0) return dayCmp;
                return earliestStart(a.getSection()).compareTo(earliestStart(b.getSection()));
            } catch (SQLException e) {
                return 0;
            }
        });
        cursor = 0;
    }

    private DayOfWeek earliestDay(Section s) throws SQLException {
        return s.getMeetingDays().stream()
                .min(Comparator.naturalOrder())
                .orElse(DayOfWeek.SUNDAY); // async sections sort to the end
    }

    private LocalTime earliestStart(Section s) throws SQLException {
        return s.getStartTimes().stream()
                .min(Comparator.naturalOrder())
                .orElse(LocalTime.MAX);
    }

    @Override public boolean hasNext() { return cursor < sorted.size(); }
    @Override public Enrollment next() { return sorted.get(cursor++); }
    @Override public void reset() { cursor = 0; }
}