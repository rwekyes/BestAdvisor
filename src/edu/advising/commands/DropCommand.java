package edu.advising.commands;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import edu.advising.audit.AuditEvent;
import edu.advising.audit.AuditLog;
import edu.advising.audit.EventType;
import edu.advising.contexts.EnrollmentContext;
import edu.advising.contexts.WaitlistContext;
import edu.advising.core.DatabaseManager;
import edu.advising.core.Table;
import edu.advising.notifications.ObservableStudent;
import edu.advising.users.Student;

import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

/**
 * DropCommand - Drop a course section
 */
@Table(name = "command_history", isSubTable = true)
public class DropCommand extends BaseCommand {
    private ObservableStudent student;
    private Section section;
    private int previousEnrollmentId;
    private DatabaseManager dbManager;

    // Adding No argument constructor needed for fromSuperType() and ORM autoMapper()
    public DropCommand() {
        this(null, null);
    }

    public DropCommand(ObservableStudent student, Section section) {
        super();
        this.commandType = "DROP";
        this.student = student;
        this.section = section;
        this.dbManager = DatabaseManager.getInstance();
    }

    public static DropCommand fromSuperType(BaseCommand base) {
        DropCommand cmd = new DropCommand();
        BaseCommand.copyBaseFields(base, cmd);
        cmd.initAfterLoad();
        return cmd;
    }

    @Override
    public void execute() {
        executionTime = LocalDateTime.now();
        try {
            Enrollment enrollment = getEnrollment();
            if (enrollment == null) {
                successful   = false;
                errorMessage = String.format("Drop failed — student not enrolled in %s",
                        section.getCourseCode());
                System.out.println("✗ " + errorMessage);
                return;
            }
            this.previousEnrollmentId = enrollment.getId();
            EnrollmentContext enrollmentContext = EnrollmentContext.load(enrollment, student, section);
            // TODO: Figure out where the heck the dropReason String comes from
            enrollmentContext.drop("dropReason"); // ENROLLED → DROPPED, persists, notifies

            executed   = true;
            successful = true;
            System.out.printf("✓ Student %s dropped %s%n",
                    student.getStudentId(), section.getCourseCode());

            try {
                promoteFromWaitlist();
            } catch (SQLException | IllegalAccessException e) {
                e.printStackTrace();
                System.out.println("Failed to promote from waitlist.");
            }
        } catch (SQLException e) {
            successful   = false;
            errorMessage = String.format("Drop failed — student not enrolled in %s",
                    section.getCourseCode());
            System.out.println("✗ " + errorMessage);
        }

        AuditLog.getInstance().log(new AuditEvent(
                0,                          // id — 0 means "not persisted yet"
                userId,
                EventType.COMMAND_EXECUTED,
                "ENROLLMENT",
                this.previousEnrollmentId,
                serializeCommandData(),
                null,
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
        try {
            Enrollment enrollment = getEnrollment();
            EnrollmentContext enrollmentContext = EnrollmentContext.load(enrollment, student, section);
            enrollmentContext.reenroll();
            System.out.printf("↶ Undone: Drop of %s - student re-enrolled%n",
                    section.getCourseCode());
            this.undoneAt = LocalDateTime.now();
            this.isUndone = true;
        } catch (SQLException e){
            e.printStackTrace();
            System.out.println("Undo failed");
        }

        AuditLog.getInstance().log(new AuditEvent(
                0,                          // id — 0 means "not persisted yet"
                userId,
                EventType.COMMAND_UNDONE,
                "ENROLLMENT",
                this.previousEnrollmentId,
                null,
                serializeCommandData(),
                null,
                LocalDateTime.now()
        ));

    }

    @Override
    public boolean isUndoable() {
        return executed && successful && section.hasCapacity();
    }

    @Override
    public String getDescription() {
        return String.format("Drop %s (%s)", section.getCourseCode(), section.getCourseName());
    }

    private void promoteFromWaitlist() throws SQLException, IllegalAccessException {
        if (!section.getWaitlist().isEmpty() && section.hasCapacity()) {
            // Get the next waitlist entry
            WaitlistEntry nextWaitlistEntry = section.getWaitlist().get(0);
            // Lookup the student for this entry
            Student student = nextWaitlistEntry.getStudent();
            // wrap in Observable
            ObservableStudent obs = ObservableStudent.fromSuperType(student);

            WaitlistContext ctx = WaitlistContext.fromEntry(
                    nextWaitlistEntry, section, obs, new CommandExecutor(student.getId())
            );
            ctx.offer(24);// Offer with a default 24hr expiry


            System.out.println(String.format("↑ Student ID %s promoted from waitlist", student.getStudentId()));

        }
    }

    @Override
    protected String serializeCommandData() {
        ObjectMapper mapper = new ObjectMapper();
        Map<String, Object> data = new HashMap<>();
        data.put("studentPk",            student.getId());   // int PK
        data.put("studentId", student.getStudentId());
        data.put("sectionId", section.getId()); // Assuming Section has an id
        data.put("previousEnrollmentId", previousEnrollmentId);
        try {
            return mapper.writeValueAsString(data);
        } catch (JsonProcessingException e) {
            throw new RuntimeException("Failed to serialize DropCommand data", e);
        }
    }

    @Override
    protected void deserializeCommandData(String json) {
        if (json == null || json.isBlank()) return;
        ObjectMapper mapper = new ObjectMapper();
        try {
            Map<String, Object> data = mapper.readValue(json, Map.class);
            int studentPk = (int) data.get("studentPk");
            int sectionId = (int) data.get("sectionId");
            this.previousEnrollmentId = (int) data.get("previousEnrollmentId");

            Student raw  = DatabaseManager.getInstance().fetchOne(Student.class, "id", studentPk);
            this.student = ObservableStudent.fromSuperType(raw);
            this.section = DatabaseManager.getInstance().fetchOne(Section.class, "id", sectionId);
        } catch (JsonProcessingException | SQLException e) {
            throw new RuntimeException("Failed to deserialize DropCommand data", e);
        }
    }

    private Enrollment getEnrollment() throws SQLException {
        String sql = "SELECT * FROM enrollments WHERE student_id = ? AND section_id = ? AND status = 'ENROLLED'";
        return dbManager.fetch(sql, rs -> {
            Enrollment e = new Enrollment();
            e.setId(rs.getInt("id"));
            e.setStudentId(rs.getInt("student_id"));
            e.setSectionId(rs.getInt("section_id"));
            e.setStatus(rs.getString("status"));
            e.setDropReason(rs.getString("drop_reason"));
            e.setFinalGrade(rs.getString("final_grade"));
            // add gradedAt if you need it
            return e;
        }, student.getId(), section.getId());
    }
}