package edu.advising.search;

import edu.advising.model.Section;

public interface SearchIterator {
    public boolean hasNext();
    public Section next();
    public void reset();
}
