package edu.advising.commands;

import com.fasterxml.jackson.core.JsonProcessingException;
import edu.advising.contexts.EnrollmentContext;
import edu.advising.contexts.RegistrationPeriodContext;
import edu.advising.core.DatabaseManager;
import edu.advising.core.Table;
import edu.advising.notifications.NotificationManager;
import edu.advising.notifications.ObservableStudent;

import java.sql.SQLException;
import java.time.LocalDateTime;

import com.fasterxml.jackson.databind.ObjectMapper;
import edu.advising.users.Student;

import java.util.HashMap;
import java.util.Map;

/**
 * RegisterCommand - Register student for a course section
 */
@Table(name = "command_history", isSubTable = true)
public class RegisterCommand extends BaseCommand {
    private ObservableStudent student;
    private Section section;
    private NotificationManager notificationManager;
    private int enrollmentId;

    // Adding No argument constructor needed for fromSuperType() and ORM autoMapper()
    public RegisterCommand() {
        this(null, null);
    }

    public RegisterCommand(ObservableStudent student, Section section) {
        super();
        this.commandType = "REGISTER";
        this.student = student;
        this.section = section;
        this.notificationManager = NotificationManager.getInstance();
    }

    @Override
    public void execute() {
        executionTime = LocalDateTime.now();

        // Check if registration is closed before continuing
        try {
            RegistrationPeriodContext periodCtx = RegistrationPeriodContext.currentPeriod(
                    section.getSemester(), section.getYear());
            if (periodCtx == null || !periodCtx.canRegister()) {
                successful = false;
                errorMessage = "Registration is not currently open for " + section.getCourseCode();
                System.out.println("✗ " + errorMessage);
                return;
            }
        } catch (SQLException e) {
            // If we can't check the period, fail safe — block registration
            successful = false;
            errorMessage = "Could not verify registration period status";
            System.out.println("✗ " + errorMessage);
            return;
        }

        if (!section.hasCapacity()) {
            successful = false;
            errorMessage = String.format("Registration failed for %s - section full", section.getCourseCode());
            System.out.println("✗ " + errorMessage);
            return;
        }

        if (hasScheduleConflict()) {
            successful = false;
            errorMessage = String.format("Registration failed for %s - schedule conflict", section.getCourseCode());
            System.out.println("✗ " + errorMessage);
            return;
        }

        try {
            EnrollmentContext enrollmentContext = EnrollmentContext.create(student, section);
            this.enrollmentId = enrollmentContext.getEnrollment().getId();
            enrollmentContext.confirm(); // PENDING → ENROLLED, persists, notifies
            executed   = true;
            successful = true;
            System.out.printf("✓ Student %s registered for %s%n",
                    student.getStudentId(), section.getCourseCode());
            // Line below is commented out to meet acceptance criteria for Enrollment State machine,
            // notification should happen in enrollmentContext.confirm()
            // notificationManager.notifyRegistration(student, section.getCourseCode(), true);
        } catch (SQLException | IllegalAccessException e) {
            successful   = false;
            errorMessage = "Already enrolled or duplicate registration prevented.";
        }
    }

    @Override
    public void undo() {
        if (!executed || !successful) {
            System.out.println("Cannot undo - command not executed or failed");
            return;
        }

        // Remove from section
        if( section.drop(student) ) {
            System.out.printf("↶ Undone: Registration for %s%n", section.getCourseCode());
            this.undoneAt = LocalDateTime.now();
            this.isUndone = true;
            // Notify about drop
            notificationManager.notifyRegistration(student, section.getCourseCode(), false);
        }
    }

    @Override
    public boolean isUndoable() {
        return executed && successful;
    }

    @Override
    public String getDescription() {
        return String.format("Register for %s (%s)", section.getCourseCode(), section.getCourseName());
    }

    private boolean hasScheduleConflict() {
        // Simplified - in real implementation, check time conflicts in student.
        return false;
    }

    @Override
    protected String serializeCommandData() {
        ObjectMapper mapper = new ObjectMapper();
        Map<String, Object> data = new HashMap<>();
        data.put("studentPk", student.getId());    //TODO: I'm not sure this is needed since my ORM handles sub-classes.
        data.put("studentId", student.getStudentId());
        data.put("sectionId", section.getId()); // Assuming Section has an id
        data.put("enrollmentId", enrollmentId);
        try {
            return mapper.writeValueAsString(data);
        } catch (JsonProcessingException e) {
            throw new RuntimeException("Failed to serialize RegisterCommand data", e);
        }
    }

    @Override
    protected void deserializeCommandData(String json) {
        ObjectMapper mapper = new ObjectMapper();
        try {
            Map<String, Object> data = mapper.readValue(json, Map.class);
            // TODO: Figure out if we have to really deal with studentPk because student is a subclass of  User.
            int studentPk = (int) data.get("studentPk");
            int sectionId = (int) data.get("sectionId");
            this.enrollmentId = (int) data.get("enrollmentId");

            // Fetch as Student (annotated), then promote to ObservableStudent
            Student raw = DatabaseManager.getInstance().fetchOne(Student.class, "id", studentPk);
            if (raw != null) {
                this.student = ObservableStudent.fromSuperType(raw);
                this.student = ObservableStudent.fromSuperType(raw);
                this.section = DatabaseManager.getInstance().fetchOne(Section.class, "id", sectionId);
            }
        } catch (JsonProcessingException | SQLException e) {
            throw new RuntimeException("Failed to deserialize RegisterCommand data", e);
        }
    }
}

