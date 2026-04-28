package edu.advising.schedule;

import edu.advising.model.Enrollment;

public interface ScheduleIterator {
    public boolean hasNext();
    public Enrollment next();
    public void reset();
}
