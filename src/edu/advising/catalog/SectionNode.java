package edu.advising.catalog;

import edu.advising.commands.Section;

import java.sql.SQLException;
import java.util.List;

public class SectionNode implements CatalogNode{

    private Section section;

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

    public String getMeetingDays(){
        return section.getSemester(); //TODO: Set up a field in the db and Section object for the meeting days
    }

    public String getStartTime(){
        return section.getSemester(); //TODO: Set up a field in the db and Section object
    }

    public String getEndTime(){
        return section.getSemester(); //TODO: Same as above
    }

    public String getRoom(){
        return section.getRoom();
    }

    public int getAvailableSeats(){
        return section.getAvailableSeats();
    }

    public String getDeliveryMode(){
        return ""; //TODO: Figure out what the delivery mode even means, Claude suggested the field in the design
    }
}
