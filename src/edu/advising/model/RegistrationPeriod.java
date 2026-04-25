package edu.advising.model;

import edu.advising.core.*;

import java.time.LocalDateTime;

@Table(name = "registration_periods")
public class RegistrationPeriod {
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
    @Column(name = "late_registration_end")
    private LocalDateTime lateRegistrationEnd;
    @Column(name = "current_state")
    private String currentState;

    // Full constructor for admin to create registration period
    public RegistrationPeriod(String semester, int year, LocalDateTime openDate, LocalDateTime closeDate, LocalDateTime lateRegistrationEnd){
        this.semester = semester;
        this.year = year;
        this.openDate = openDate;
        this.closeDate = closeDate;
        this.lateRegistrationEnd = lateRegistrationEnd;
        this.currentState = "NOT_OPEN";
    }

    // Empty constructor for student fetching
    public RegistrationPeriod(){}

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getSemester() {
        return semester;
    }

    public void setSemester(String semester) {
        this.semester = semester;
    }

    public int getYear() {
        return year;
    }

    public void setYear(int year) {
        this.year = year;
    }

    public LocalDateTime getOpenDate() {
        return openDate;
    }

    public void setOpenDate(LocalDateTime openDate) {
        this.openDate = openDate;
    }

    public LocalDateTime getCloseDate() {
        return closeDate;
    }

    public void setCloseDate(LocalDateTime closeDate) {
        this.closeDate = closeDate;
    }

    public LocalDateTime getLateRegistrationEnd() {
        return lateRegistrationEnd;
    }

    public void setLateRegistrationEnd(LocalDateTime lateRegistrationEnd) {
        this.lateRegistrationEnd = lateRegistrationEnd;
    }

    public String getCurrentState() {
        return currentState;
    }

    public void setCurrentState(String currentState) {
        this.currentState = currentState;
    }
}
