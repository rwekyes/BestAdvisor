package edu.advising.contexts;

import edu.advising.commands.Enrollment;
import edu.advising.commands.Section;
import edu.advising.core.DatabaseManager;
import edu.advising.notifications.NotificationManager;
import edu.advising.states.EnrollmentState;
import edu.advising.states.StateFactory;
import edu.advising.users.Student;

import java.sql.SQLException;

public class EnrollmentContext {

    private Enrollment enrollment;
    private EnrollmentState state;
    private Section section;
    private NotificationManager notificationManager;
    private Student student;

    private EnrollmentContext(Enrollment enrollment, Student student, Section section) {
        this.enrollment = enrollment;
        this.section = section;
        this.state = StateFactory.enrollmentStateFor(enrollment.getStatus());
        this.notificationManager = NotificationManager.getInstance();
        this.student = student;
    }

    public static EnrollmentContext create(Student student, Section section) throws SQLException, IllegalAccessException {
        Enrollment e = new Enrollment(student.getId(), section.getId());
        DatabaseManager.getInstance().upsert(e);
        return new EnrollmentContext(e, student, section);
    }

    public static EnrollmentContext load(Enrollment enrollment, Student student, Section section) {
        return new EnrollmentContext(enrollment, student, section);
    }

    public void setState(EnrollmentState newState) {
        this.state = newState;
        this.enrollment.setStatus(newState.getStatusName());
    }

    private void persist() {
        if (enrollment.getId() == 0) return;  // in-memory only, no DB row to update
        try {
            DatabaseManager.getInstance().upsert(enrollment);
        } catch (SQLException | IllegalAccessException e) {
            throw new RuntimeException("Failed to persist enrollment state - ", e);
        }
    }

    public EnrollmentState getState() { return state; }
    public Enrollment getEnrollment() { return enrollment; }
    public Section getSection()       { return section; }
    public NotificationManager getNotificationManager() { return notificationManager; }

    public void confirm() {
        state.confirm(this);
        persist();
    }
    public void drop(String reason) throws SQLException{
        state.drop(this, reason);
        persist();
    }
    public void withdraw() throws SQLException{
        state.withdraw(this);
        persist();
    }
    public void complete(String finalGrade) {
        state.complete(this, finalGrade);
        persist();
    }
    public void reenroll() {
        state.reenroll(this);
        persist();
    }

    public boolean canDrop()      { return state.canDrop(); }
    public boolean canWithdraw()  { return state.canWithdraw(); }
    public boolean canComplete()  { return state.canComplete(); }
    public boolean canReenroll()  { return state.canReenroll(); }


    public Student getStudent() {
        return student;
    }

    public void setStudent(Student student) {
        this.student = student;
    }
}
