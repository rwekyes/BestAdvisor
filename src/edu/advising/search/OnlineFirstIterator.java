package edu.advising.search;

import edu.advising.model.Section;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Set;

public class OnlineFirstIterator implements SearchIterator {
    private static final Set<String> ONLINE_MODES = Set.of("ONLINE", "ASYNC");

    private final List<Section> sorted;
    private int cursor;

    public OnlineFirstIterator(List<Section> sections) {
        sorted = new ArrayList<>(sections);
        sorted.sort(Comparator
                .comparing((Section s) -> !ONLINE_MODES.contains(s.getDeliveryMethod().toUpperCase()))
                .thenComparing(Section::getCourseCode, String.CASE_INSENSITIVE_ORDER));
        cursor = 0;
    }

    @Override public boolean hasNext() { return cursor < sorted.size(); }
    @Override public Section next() { return sorted.get(cursor++); }
    @Override public void reset() { cursor = 0; }
}