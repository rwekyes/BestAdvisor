package edu.advising.commands;

/*
Faculty Permission - Represents a request to a faculty member to approve a waitlisted student
 */

import edu.advising.core.*;

import java.time.LocalDateTime;
import java.util.List;

@Table(name = "faculty_permissions")
public class FacultyPermission {

    @Id(isPrimary = true)
    @Column(name = "id", upsertIgnore = true)
    private int id;
    @Id
    @Column(name = "section_id", foreignKey = true)
    private int sectionId;
    @Id
    @Column(name = "waitlist_id", foreignKey = true)
    private int waitlistId;
    @Column(name = "request_date")
    private LocalDateTime requestDate;
    @Column(name = "expiry_date")
    private LocalDateTime expiryDate;
    @Column(name = "deny_reason")
    private String denyReason;
    @Column(name = "status")
    private String status;
    @ManyToOne(targetEntity = Section.class, joinColumn = "section_id")
    private Section section;
    @ManyToOne(targetEntity = WaitlistEntry.class, joinColumn = "waitlist_id")
    private WaitlistEntry waitlistEntry;

    public FacultyPermission(){}

    public void setId(int id){
        this.id = id;
    }

    public int getId(){
        return id;
    }

    public int getSectionId() {
        return sectionId;
    }

    public void setSectionId(int sectionId) {
        this.sectionId = sectionId;
    }

    public int getWaitlistId() {
        return waitlistId;
    }

    public void setWaitlistId(int waitlistId) {
        this.waitlistId = waitlistId;
    }

    public LocalDateTime getExpiryDate() {
        return expiryDate;
    }

    public void setExpiryDate(LocalDateTime expiryDate) {
        this.expiryDate = expiryDate;
    }

    public LocalDateTime getRequestDate() {
        return requestDate;
    }

    public void setRequestDate(LocalDateTime requestDate) {
        this.requestDate = requestDate;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Section getSection() {
        return section;
    }

    public void setSection(Section section) {
        this.section = section;
    }

    public WaitlistEntry getWaitlistEntry() {
        return waitlistEntry;
    }

    public void setWaitlistEntry(WaitlistEntry waitlistEntry) {
        this.waitlistEntry = waitlistEntry;
    }

    public String getDenyReason() {
        return denyReason;
    }

    public void setDenyReason(String denyReason) {
        this.denyReason = denyReason;
    }
}
