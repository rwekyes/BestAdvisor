package edu.advising.commands;

// ============================================================================
// ADMIN USER MANAGEMENT — AdminCreateUserCommand (Concrete Command)
// ============================================================================
//
// Creates a new Student, Faculty, or Admin account.
// Undo deactivates the created account rather than hard-deleting it, because
// the user record may already be referenced by enrollment/audit tables.
//
// GUI WIRING:
//   AdminCreateUserCommand cmd = new AdminCreateUserCommand(
//       "STUDENT", username, password, email, first, last, studentId
//   );
//   executor.execute(cmd);
//   if (!cmd.wasSuccessful()) showError(cmd.getErrorMessage());
//   else refreshUserTable();
//
// ============================================================================

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import edu.advising.audit.AuditEvent;
import edu.advising.audit.AuditLog;
import edu.advising.audit.EventType;
import edu.advising.core.DatabaseManager;
import edu.advising.core.Table;
import edu.advising.users.User;
import edu.advising.users.UserFactory;

import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Table(name = "command_history", isSubTable = true)
public class AdminCreateUserCommand extends BaseCommand {

    private String userType;
    private String username;
    private String password;
    private String email;
    private String firstName;
    private String lastName;
    private List<String> extraFields;   // e.g. [studentId] or [employeeId, department]
    private int createdUserId = 0;

    private final UserFactory userFactory = new UserFactory();

    public AdminCreateUserCommand() {
        this("STUDENT", "", "", "", "", "");
    }

    /**
     * @param extraFields For STUDENT: [studentId].
     *                    For FACULTY: [employeeId, department].
     *                    For ADMIN:   [accessLevel].
     */
    public AdminCreateUserCommand(String userType, String username, String password,
                                   String email, String firstName, String lastName,
                                   String... extraFields) {
        super();
        this.commandType = "ADMIN_CREATE_USER";
        this.userType    = userType;
        this.username    = username;
        this.password    = password;
        this.email       = email;
        this.firstName   = firstName;
        this.lastName    = lastName;
        this.extraFields = Arrays.asList(extraFields);
    }

    public static AdminCreateUserCommand fromSuperType(BaseCommand base) {
        AdminCreateUserCommand cmd = new AdminCreateUserCommand();
        BaseCommand.copyBaseFields(base, cmd);
        cmd.initAfterLoad();
        return cmd;
    }

    // -------------------------------------------------------------------------
    // Command Interface
    // -------------------------------------------------------------------------

    @Override
    public void execute() {
        executionTime = LocalDateTime.now();
        try {
            User created = userFactory.createUser(
                userType, username, password, email, firstName, lastName,
                extraFields.toArray(new String[0])
            );
            createdUserId = created.getId();
            executed      = true;
            successful    = true;
            System.out.printf("✓ Admin created %s user: %s %s (ID %d)%n",
                userType, firstName, lastName, createdUserId);
        } catch (Exception e) {
            successful   = false;
            errorMessage = "Failed to create user: " + e.getMessage();
            System.err.println("✗ " + errorMessage);
        }

        AuditLog.getInstance().log(new AuditEvent(
            0, userId, EventType.COMMAND_EXECUTED,
            "USER", createdUserId, null, serializeCommandData(), null, LocalDateTime.now()
        ));
    }

    @Override
    public void undo() {
        if (!executed || !successful || createdUserId == 0) {
            System.out.println("Cannot undo — user was never created.");
            return;
        }
        try {
            DatabaseManager.getInstance().executeUpdate(
                "UPDATE users SET is_active = FALSE WHERE id = ?", createdUserId);
            this.undoneAt = LocalDateTime.now();
            this.isUndone = true;
            System.out.printf("↶ Deactivated newly created user (ID %d)%n", createdUserId);
        } catch (SQLException e) {
            System.err.println("✗ Undo create failed: " + e.getMessage());
        }

        AuditLog.getInstance().log(new AuditEvent(
            0, userId, EventType.COMMAND_UNDONE,
            "USER", createdUserId, serializeCommandData(), null, null, LocalDateTime.now()
        ));
    }

    @Override
    public boolean isUndoable() { return executed && successful && createdUserId > 0; }

    @Override
    public String getDescription() {
        return String.format("Admin create %s: %s %s (%s)", userType, firstName, lastName, username);
    }

    // -------------------------------------------------------------------------
    // Serialization
    // -------------------------------------------------------------------------

    @Override
    protected String serializeCommandData() {
        ObjectMapper mapper = new ObjectMapper();
        Map<String, Object> data = new HashMap<>();
        data.put("userType",      userType);
        data.put("username",      username);
        data.put("email",         email);
        data.put("firstName",     firstName);
        data.put("lastName",      lastName);
        data.put("extraFields",   extraFields);
        data.put("createdUserId", createdUserId);
        try {
            return mapper.writeValueAsString(data);
        } catch (JsonProcessingException e) {
            throw new RuntimeException("AdminCreateUserCommand: serialization failed", e);
        }
    }

    @Override
    protected void deserializeCommandData(String json) {
        if (json == null || json.isBlank()) return;
        ObjectMapper mapper = new ObjectMapper();
        try {
            Map<String, Object> data = mapper.readValue(json, Map.class);
            this.userType      = (String) data.get("userType");
            this.username      = (String) data.get("username");
            this.email         = (String) data.get("email");
            this.firstName     = (String) data.get("firstName");
            this.lastName      = (String) data.get("lastName");
            this.extraFields   = mapper.convertValue(data.get("extraFields"),
                                     new TypeReference<List<String>>() {});
            Object uid = data.get("createdUserId");
            this.createdUserId = uid instanceof Integer i ? i : 0;
        } catch (JsonProcessingException e) {
            throw new RuntimeException("Failed to deserialize AdminCreateUserCommand", e);
        }
    }
}
