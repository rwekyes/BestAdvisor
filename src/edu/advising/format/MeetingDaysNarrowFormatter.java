package edu.advising.format;

import java.time.DayOfWeek;
import java.time.format.TextStyle;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class MeetingDaysNarrowFormatter implements MeetingDaysFormatter{
    public static final MeetingDaysNarrowFormatter instance = new MeetingDaysNarrowFormatter();
    @Override
    public String format(List<DayOfWeek> list){
        List<DayOfWeek> newList = new ArrayList<>(list);
        String formatted = "";
        for(DayOfWeek day : newList){
            formatted += formatted + day.getDisplayName(TextStyle.NARROW, Locale.getDefault());
            if(list.iterator().hasNext()) {
                formatted += formatted + "/";
            }
        }
        return formatted;
    }
}
