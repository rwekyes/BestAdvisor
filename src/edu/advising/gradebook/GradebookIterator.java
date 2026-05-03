package edu.advising.gradebook;

import edu.advising.model.Enrollment;

public interface GradebookIterator {
    boolean hasNext();
    Enrollment next();
    void reset();
}
