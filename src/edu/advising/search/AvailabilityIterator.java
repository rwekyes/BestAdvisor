package edu.advising.search;

import edu.advising.model.Section;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class AvailabilityIterator implements SearchIterator {
    private final List<Section> sorted;
    private int cursor;

    public AvailabilityIterator(List<Section> sections) {
        sorted = new ArrayList<>(sections);
        sorted.sort(Comparator
                .comparing((Section s) -> !s.hasCapacity())        // open sections first
                .thenComparingInt(s -> -(s.getCapacity() - s.getEnrolled())));  // most seats available first
        cursor = 0;
    }

    @Override public boolean hasNext() { return cursor < sorted.size(); }
    @Override public Section next() { return sorted.get(cursor++); }
    @Override public void reset() { cursor = 0; }
}