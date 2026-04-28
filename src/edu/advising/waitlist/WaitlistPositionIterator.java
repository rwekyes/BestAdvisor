package edu.advising.waitlist;

import edu.advising.model.WaitlistEntry;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class WaitlistPositionIterator implements WaitlistIterator{

    private List<WaitlistEntry> sorted;

    private int cursor;

    public WaitlistPositionIterator(List<WaitlistEntry> waitlistEntries){
        sorted = new ArrayList<WaitlistEntry>(waitlistEntries);
        sorted.sort(Comparator.comparing(WaitlistEntry::getPosition));
        cursor = 0;
    }

    @Override
    public boolean hasNext() {
        return cursor < sorted.size();
    }

    @Override
    public WaitlistEntry next() {
        if (!hasNext()) {
            System.err.println("Waitlist has no more elements");
        }
        return sorted.get(cursor++);
    }

    @Override
    public void reset() {
        cursor = 0;
    }
}
