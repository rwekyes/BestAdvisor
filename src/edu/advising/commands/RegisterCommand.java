package edu.advising.commands;

import com.fasterxml.jackson.core.JsonProcessingException;
import edu.advising.audit.AuditEvent;
import edu.advising.audit.AuditLog;
import edu.advising.audit.EventType;
import edu.advising.common.PipelineResult;
import edu.advising.core.DatabaseManager;
import edu.advising.core.Table;
import edu.advising.model.Section;
import edu.advising.notifications.NotificationManager;
import edu.advising.notifications.ObservableStudent;

import java.sql.SQLException;
import java.time.LocalDateTime;

import com.fasterxml.jackson.databind.ObjectMapper;
import edu.advising.permissions.PermissionTree;
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
    private PermissionTree permissionTree;
    private NotificationManager notificationManager;
    private int enrollmentId;

    // Adding No argument constructor needed for fromSuperType() and ORM autoMapper()
    public RegisterCommand() {
        this(null, null, null);
    }

    public RegisterCommand(ObservableStudent student, Section section, PermissionTree permissionTree) {
        super();
        this.commandType = "REGISTER";
        this.student = student;
        this.section = section;
        this.permissionTree = permissionTree;
        this.notificationManager = NotificationManager.getInstance();
    }

    @Override
    public void execute() {
        executionTime = LocalDateTime.now();
        RegistrationContext ctx = new RegistrationContext(student, section, permissionTree);

        PipelineResult result = RegistrationPipeline.standard().run(ctx);

        successful = result.isPassed();
        executed = result.isPassed();
        errorMessage = result.isPassed() ? null : result.getErrorMessage();

        if (result.isPassed()) {
            this.enrollmentId = ctx.getEnrollment().getId();
        }

        AuditLog.getInstance().log(new AuditEvent(
                0,                          // id — 0 means "not persisted yet"
                userId,
                EventType.COMMAND_EXECUTED,
                "ENROLLMENT",
                this.enrollmentId,
                null,
                serializeCommandData(),
                null,
                LocalDateTime.now()
        ));

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

        AuditLog.getInstance().log(new AuditEvent(
                0,                          // id — 0 means "not persisted yet"
                userId,
                EventType.COMMAND_UNDONE,
                "ENROLLMENT",
                this.enrollmentId,
                serializeCommandData(),
                null,
                null,
                LocalDateTime.now()
        ));
    }

    @Override
    public boolean isUndoable() {
        return executed && successful;
    }

    @Override
    public String getDescription() {
        return String.format("Register for %s (%s)", section.getCourseCode(), section.getCourseName());
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

