package edu.advising.search;

import edu.advising.model.Section;
import edu.advising.users.Faculty;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class InstructorIterator implements SearchIterator {
    private final List<Section> sorted;
    private int cursor;

    public InstructorIterator(List<Section> sections) {
        sorted = new ArrayList<>(sections);

        // Map the sections to the Faculty's last name first, catching the SQLException
        Map<Integer, String> lastNameBySectionId = new HashMap<>();
        for (Section s : sections) {
            try {
                Faculty f = s.getFaculty();
                lastNameBySectionId.put(s.getId(), f != null ? f.getLastName() : "");
            } catch (SQLException e) {
                lastNameBySectionId.put(s.getId(), "ZZZZZ"); // Fallback case - unretrieved name becomes all zzs and hits the bottom
            }
        }

        // Then sort the mapped list
        sorted.sort(Comparator.comparing(
                s -> lastNameBySectionId.getOrDefault(s.getId(), ""),
                String.CASE_INSENSITIVE_ORDER
        ));
        cursor = 0;
    }

    @Override public boolean hasNext() { return cursor < sorted.size(); }
    @Override public Section next() { return sorted.get(cursor++); }
    @Override public void reset() { cursor = 0; }
}