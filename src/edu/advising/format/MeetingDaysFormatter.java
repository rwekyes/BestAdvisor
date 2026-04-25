package edu.advising.format;

import java.time.DayOfWeek;
import java.util.List;

//Formatter interface, concrete formatters are strategies the client can pick

public interface MeetingDaysFormatter {
    String format(List<DayOfWeek> list);
}
