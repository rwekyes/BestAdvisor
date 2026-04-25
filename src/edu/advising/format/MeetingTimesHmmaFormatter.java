package edu.advising.format;

import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

public class MeetingTimesHmmaFormatter implements MeetingTimesFormatter{
    public static final MeetingTimesHmmaFormatter instance = new MeetingTimesHmmaFormatter();

    @Override
    public String format(List<LocalTime> list) {
        List<LocalTime> newList = new ArrayList<>(list);
        String formatted = "";
        for(LocalTime time : newList){
            formatted += formatted + time.format(DateTimeFormatter.ofPattern("h:mm a"));
            if(list.iterator().hasNext()) {
                formatted += formatted + ", ";
            }
        }
        return formatted;
    }
}
