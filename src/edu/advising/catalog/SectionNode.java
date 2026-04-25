package edu.advising.catalog;

import edu.advising.format.MeetingDaysAbbreviatedFormatter;
import edu.advising.format.MeetingTimesHmmaFormatter;
import edu.advising.model.Section;

import java.sql.SQLException;
import java.util.List;

public class SectionNode implements CatalogNode{

    private Section section;

    public SectionNode(Section section){
        this.section = section;
    }

    @Override
    public String getDisplayName() {
        return section.getCourseName();
    }

    @Override
    public double getTotalCredits() {
        return 0;
    }

    @Override
    public int getTotalSections() {
        return 1;
    }

    @Override
    public List<CatalogNode> getChildren() {
        return null;
    }

    @Override
    public boolean isLeaf() {
        return true;
    }


    public Section getSection() {
        return section;
    }

    public String getInstructor() throws SQLException {
        return section.getFaculty().getFullName();
    }

    public String getMeetingDays() throws SQLException {
        return MeetingDaysAbbreviatedFormatter.instance.format(section.getMeetingDays());
    }

    public String getStartTime() throws SQLException {
        return MeetingTimesHmmaFormatter.instance.format(section.getStartTimes());
    }

    public String getEndTime() throws SQLException {
        return MeetingTimesHmmaFormatter.instance.format(section.getEndTimes());
    }

    public String getRoom(){
        return section.getRoom();
    }

    public int getAvailableSeats(){
        return section.getAvailableSeats();
    }

    public String getDeliveryMode(){
        return section.getDeliveryMethod();
    }
}
