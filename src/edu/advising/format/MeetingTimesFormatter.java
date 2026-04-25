package edu.advising.format;

import java.time.LocalTime;
import java.util.List;

// Interface to format the meeting times, currently I only have one format for them, but new formats can be thrown in easy-peasy

public interface MeetingTimesFormatter {
    String format(List<LocalTime> list);
}
