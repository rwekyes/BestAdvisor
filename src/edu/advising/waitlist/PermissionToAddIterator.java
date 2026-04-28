package edu.advising.waitlist;

import edu.advising.model.FacultyPermission;
import edu.advising.model.WaitlistEntry;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

//Only returns WaitlistEntry objects that also have faculty permission
public class PermissionToAddIterator implements WaitlistIterator{

    private final List<WaitlistEntry> filtered;
    private int cursor;

    public PermissionToAddIterator(List<WaitlistEntry> entries, List<FacultyPermission> permissions) {
        Set<Integer> approvedIds = permissions.stream()
                .filter(fp -> "APPROVED".equals(fp.getStatus()))
                .map(FacultyPermission::getWaitlistId)
                .collect(Collectors.toSet());

        filtered = entries.stream()
                .filter(we -> approvedIds.contains(we.getId()))
                .collect(Collectors.toList());

        cursor = 0;
    }

    @Override
    public boolean hasNext() {
        return cursor < filtered.size();
    }

    @Override
    public WaitlistEntry next() {
        if (!hasNext()) {
            System.err.println("Waitlist has no more elements");
        }
        return filtered.get(cursor++);
    }

    @Override
    public void reset() {
        cursor = 0;
    }
}
