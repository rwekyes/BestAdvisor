package edu.advising.model;

import edu.advising.core.*;

import java.time.DayOfWeek;
import java.time.LocalTime;

@Table(name = "section_meeting_times")
public class SectionMeetingTime {
    @Id(isPrimary = true)
    @Column(name = "id", upsertIgnore = true)
    private int id;
    @Id
    @Column(name = "section_id")
    private int sectionId;
    @Column(name = "day_of_week")
    private String dayOfWeek;
    @Column(name ="start_time")
    private LocalTime startTime;
    @Column(name = "end_time")
    private LocalTime endTime;
    @Column(name = "room")
    private String room;

    public SectionMeetingTime(){}

    public SectionMeetingTime(int id, int sectionId, String dayOfWeek, LocalTime startTime, LocalTime endTime, String room){
        this.id = id;
        this.sectionId = sectionId;
        this.dayOfWeek = dayOfWeek;
        this.startTime = startTime;
        this.endTime = endTime;
        this.room = room;
    }

    public int getId() {
        return id;
    }

    public int getSectionId() {
        return sectionId;
    }

    public void setSectionId(int sectionId) {
        this.sectionId = sectionId;
    }

    public DayOfWeek getDayOfWeek() {
        return DayOfWeek.valueOf(dayOfWeek.toUpperCase()); // Convert the db string to a day object and return it
    }

    public void setDayOfWeek(String dayOfWeek) {
        this.dayOfWeek = dayOfWeek;
    }

    public LocalTime getStartTime() {
        return startTime;
    }

    public void setStartTime(LocalTime startTime) {
        this.startTime = startTime;
    }

    public LocalTime getEndTime() {
        return endTime;
    }

    public void setEndTime(LocalTime endTime) {
        this.endTime = endTime;
    }

    public String getRoom() {
        return room;
    }

    public void setRoom(String room) {
        this.room = room;
    }
}
