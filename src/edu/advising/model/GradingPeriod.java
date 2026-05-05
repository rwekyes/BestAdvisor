package edu.advising.model;

import edu.advising.core.*;

import java.time.LocalDateTime;

@Table(name = "grading_periods")
public class GradingPeriod {
    @Id(isPrimary = true)
    @Column(name = "id", upsertIgnore = true)
    private int id;
    @Column(name = "semester")
    private String semester;
    @Column(name = "year")
    private int year;
    @Column(name = "open_date")
    private LocalDateTime openDate;
    @Column(name = "close_date")
    private LocalDateTime closeDate;
    @Column(name = "current_state")
    private String currentState;

    public GradingPeriod(String semester, int year, LocalDateTime openDate, LocalDateTime closeDate) {
        this.semester = semester;
        this.year = year;
        this.openDate = openDate;
        this.closeDate = closeDate;
        this.currentState = "NOT_OPEN";
    }

    public GradingPeriod() {}

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getSemester() { return semester; }
    public void setSemester(String semester) { this.semester = semester; }

    public int getYear() { return year; }
    public void setYear(int year) { this.year = year; }

    public LocalDateTime getOpenDate() { return openDate; }
    public void setOpenDate(LocalDateTime openDate) { this.openDate = openDate; }

    public LocalDateTime getCloseDate() { return closeDate; }
    public void setCloseDate(LocalDateTime closeDate) { this.closeDate = closeDate; }

    public String getCurrentState() { return currentState; }
    public void setCurrentState(String currentState) { this.currentState = currentState; }
}
