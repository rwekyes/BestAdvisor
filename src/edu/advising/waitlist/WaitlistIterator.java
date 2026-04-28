package edu.advising.waitlist;

import edu.advising.model.WaitlistEntry;

public interface WaitlistIterator {
    public boolean hasNext();
    public WaitlistEntry next();
    public void reset();
}
