package edu.advising.schedule;

import edu.advising.model.Enrollment;

import java.sql.SQLException;
import java.util.List;
import java.util.stream.Collectors;

public class FilteredIterator implements ScheduleIterator {
    private final List<Enrollment> filtered;
    private int cursor;

    private FilteredIterator(List<Enrollment> filtered) {
        this.filtered = filtered;
        this.cursor = 0;
    }

    public static FilteredIterator byStatus(List<Enrollment> enrollments, String status) {
        return new FilteredIterator(
                enrollments.stream()
                        .filter(e -> status.equalsIgnoreCase(e.getStatus()))
                        .collect(Collectors.toList())
        );
    }

    public static FilteredIterator byDelivery(List<Enrollment> enrollments, String deliveryMethod) {
        return new FilteredIterator(
                enrollments.stream()
                        .filter(e -> {
                            try {
                                return deliveryMethod.equalsIgnoreCase(e.getSection().getDeliveryMethod());
                            } catch (SQLException ex) {
                                return false;
                            }
                        })
                        .collect(Collectors.toList())
        );
    }

    @Override public boolean hasNext() { return cursor < filtered.size(); }
    @Override public Enrollment next() { return filtered.get(cursor++); }
    @Override public void reset() { cursor = 0; }
}