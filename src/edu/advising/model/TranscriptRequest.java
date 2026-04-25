package edu.advising.model;

import edu.advising.core.*;
import edu.advising.users.Student;

import java.math.BigDecimal;
import java.sql.SQLException;
import java.time.LocalDateTime;

/*
Transcript Request - represents a... transcript request
*/

@Table(name = "transcript_requests")
public class TranscriptRequest {
    @Id(isPrimary = true)
    @Column(name = "id", upsertIgnore = true)
    private int id;
    @Id
    @Column(name = "student_id", foreignKey = true)
    private int studentId;
    @Column(name = "request_type")
    private String requestType;
    @Column(name = "recipient_name")
    private String recipientName;
    @Column(name = "recipient_address")
    private String recipientAddress;
    @Column(name = "request_date")
    private LocalDateTime requestDate;
    @Column(name = "status")
    private String status;
    @Column(name = "tracking_number")
    private String trackingNumber;
    @Column(name = "fee")
    private BigDecimal fee;
    @Column(name = "is_rush")
    private boolean isRush;
    @Column(name = "completed_date")
    private LocalDateTime completedAt;
    @Column(name = "failure_reason")
    private String failureReason;
    @Column(name = "processed_by")
    private int processdBy;

    @ManyToOne(targetEntity = Student.class, joinColumn = "student_id")
    private Student student;

    public TranscriptRequest(){
        throw new IllegalStateException("Transcript request cannot be created without a student or student id");
    }

    public TranscriptRequest(Student student){
        this.studentId = student.getId();
        this.student = student;
    }

    public TranscriptRequest(int studentId) throws SQLException{
        this.student = DatabaseManager.getInstance()
                .fetchOne(Student.class, "id", studentId);
    }

    public void setStatus(String statusName) {
        this.status = statusName;
    }

    public String getStatus(){return status;}

    public void setId(int id) {
        this.id = id;
    }

    public int getId() {
        return id;
    }

    public void setTrackingNumber(String trackingNumber) {
        this.trackingNumber = trackingNumber;
    }

    public String getTrackingNumber(){return trackingNumber;}

    public void setStudent(Student student) {
        this.studentId = student.getId();
        this.student = student;
    }

    public Student getStudent() throws SQLException {
        if (this.student == null) {
            // Lazy Load: Use the generic fetchOne from DatabaseManager
            this.student = DatabaseManager.getInstance()
                    .fetchOne(Student.class, "id", this.studentId);
        }
        return (this.student != null) ? this.student : null;
    }

    public String getRequestType() {
        return requestType;
    }

    public void setRequestType(String requestType) {
        this.requestType = requestType;
    }

    public String getRecipientName() {
        return recipientName;
    }

    public void setRecipientName(String recipientName) {
        this.recipientName = recipientName;
    }

    public String getRecipientAddress() {
        return recipientAddress;
    }

    public void setRecipientAddress(String recipientAddress) {
        this.recipientAddress = recipientAddress;
    }

    public LocalDateTime getRequestDate() {
        return requestDate;
    }

    public void setRequestDate(LocalDateTime requestDate) {
        this.requestDate = requestDate;
    }

    public BigDecimal getFee() {
        return fee;
    }

    public void setFee(BigDecimal fee) {
        this.fee = fee;
    }

    public boolean isRush() {
        return isRush;
    }

    public void setRush(boolean rush) {
        isRush = rush;
    }

    public LocalDateTime getCompletedAt() {
        return completedAt;
    }

    public void setCompletedAt(LocalDateTime completedAt) {
        this.completedAt = completedAt;
    }

    public String getFailureReason() {
        return failureReason;
    }

    public void setFailureReason(String failureReason) {
        this.failureReason = failureReason;
    }

    public int getProcessdBy() {
        return processdBy;
    }

    public void setProcessdBy(int processdBy) {
        this.processdBy = processdBy;
    }
}
