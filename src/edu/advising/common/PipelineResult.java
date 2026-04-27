package edu.advising.common;

import edu.advising.format.MeetingDaysAbbreviatedFormatter;
import edu.advising.format.MeetingTimesHmmaFormatter;
import edu.advising.model.Section;

import java.sql.SQLException;

public class PipelineResult {
    private final boolean passed;
    private final String failedAt;
    private final String errorMessage;
    private final Section conflictingSection;

    private PipelineResult(boolean passed, String failedAt, String errorMessage, Section conflictingSection){
        this.passed = passed;
        this.failedAt = failedAt;
        this.errorMessage = errorMessage;
        this.conflictingSection = conflictingSection;
    }

    public static PipelineResult passed(){
        return new PipelineResult(true, null, null, null);
    }

    public static PipelineResult failed(String failedAt, String message){
        return new PipelineResult(false, failedAt, message, null);
    }
    public static PipelineResult failedWithConflict(String failedAt, Section conflict) {
        String message = "";
                try{
                    message += String.format("Time conflict with - " +
                            conflict.getCourseCode() + " - " +
                            MeetingDaysAbbreviatedFormatter.instance.format(conflict.getMeetingDays()) + " - " +
                            MeetingTimesHmmaFormatter.instance.format(conflict.getStartTimes()) + " - " +
                            MeetingTimesHmmaFormatter.instance.format(conflict.getEndTimes())
                            );
                }catch (SQLException e){
                    message = "Time conflict - error fetching conflicting section - " + e.getMessage();
                }
        return new PipelineResult(false, failedAt, message, conflict);
    }
    public boolean isPassed(){
        return passed;
    }
    public String getFailedAt(){
        return failedAt;
    }
    public String getErrorMessage(){
        return errorMessage;
    }

    public Section getConflictingSection() {
        return conflictingSection;
    }
}
