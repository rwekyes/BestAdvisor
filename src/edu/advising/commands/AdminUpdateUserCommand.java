package edu.advising.commands;

// ============================================================================
// ADMIN USER MANAGEMENT — AdminUpdateUserCommand (Concrete Command)
// ============================================================================
//
// Updates a single named field on any user type.  The old value is captured at
// construction time so undo() is always reliable.
//
// SUPPORTED FIELDS
//   Base (all users): firstName, lastName, email, phone, username, password
//   Student only:     gpa, major, minor, classification, academicStanding,
//                     enrollmentStatus, creditsEarned
//   Faculty only:     department, title, officeLocation, officeHours
//   Admin only:       accessLevel
//
// MULTI-FIELD EDITS
//   The edit form wraps multiple AdminUpdateUserCommands in a MacroCommand so
//   the entire "Save" action is one undoable unit:
//
//   MacroCommand macro = new MacroCommand("Edit user " + user.getFullName());
//   for (String field : changedFields) {
//       macro.addCommand(new AdminUpdateUserCommand(typedUser, field, newValues.get(field)));
//   }
//   executor.execute(macro);
//
// IMPORTANT: pass the *typed* user object (Student/Faculty/Admin), not a plain
//   User, so that dbManager.upsert() also writes the subtype table.
//
// ============================================================================

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import edu.advising.audit.AuditEvent;
import edu.advising.audit.AuditLog;
import edu.advising.audit.EventType;
import edu.advising.core.DatabaseManager;
import edu.advising.core.Table;
import edu.advising.users.*;

import java.math.BigDecimal;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

@Table(name = "command_history", isSubTable = true)
public class AdminUpdateUserCommand extends BaseCommand {

    private User   targetUser;
    private String fieldName;
    private String newValue;
    private String oldValue;        // snapshot captured at construction
    private int    targetUserId;
    private String targetUserType;

    private final DatabaseManager dbManager = DatabaseManager.getInstance();
    private final UserFactory     factory   = new UserFactory();

    public AdminUpdateUserCommand() {
        this(new User(), "firstName", "");
    }

    /**
     * @param targetUser The live, typed user object whose field will be changed.
     * @param fieldName  One of the supported field names listed above.
     * @param newValue   The desired new value (always a String; numeric fields are parsed on apply).
     */
    public AdminUpdateUserCommand(User targetUser, String fieldName, String newValue) {
        super();
        this.commandType    = "ADMIN_UPDATE_USER";
        this.targetUser     = targetUser;
        this.fieldName      = fieldName;
        this.newValue       = newValue;
        this.oldValue       = readField(targetUser, fieldName);
        this.targetUserId   = targetUser.getId();
        this.targetUserType = targetUser.getUserType();
    }

    public static AdminUpdateUserCommand fromSuperType(BaseCommand base) {
        AdminUpdateUserCommand cmd = new AdminUpdateUserCommand();
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
            applyField(targetUser, fieldName, newValue);
            targetUser.setUpdatedAt(LocalDateTime.now());
            dbManager.upsert(targetUser);   // runtime type drives which table(s) are written
            executed   = true;
            successful = true;
            System.out.printf("✓ Admin updated %s.%s for user %d: [%s] → [%s]%n",
                targetUserType, fieldName, targetUserId, oldValue, newValue);
        } catch (Exception e) {
            applyField(targetUser, fieldName, oldValue); // roll back in-memory
            successful   = false;
            errorMessage = "Update failed: " + e.getMessage();
            System.err.println("✗ " + errorMessage);
        }

        AuditLog.getInstance().log(new AuditEvent(
            0, userId, EventType.COMMAND_EXECUTED, "USER", targetUserId,
            serializeOld(), serializeNew(), null, LocalDateTime.now()
        ));
    }

    @Override
    public void undo() {
        if (!executed || !successful) {
            System.out.println("Cannot undo — update was not completed.");
            return;
        }
        try {
            applyField(targetUser, fieldName, oldValue);
            targetUser.setUpdatedAt(LocalDateTime.now());
            dbManager.upsert(targetUser);
            this.undoneAt = LocalDateTime.now();
            this.isUndone = true;
            System.out.printf("↶ Restored %s.%s for user %d to [%s]%n",
                targetUserType, fieldName, targetUserId, oldValue);
        } catch (Exception e) {
            applyField(targetUser, fieldName, newValue); // re-apply if undo fails
            System.err.println("✗ Undo failed: " + e.getMessage());
        }

        AuditLog.getInstance().log(new AuditEvent(
            0, userId, EventType.COMMAND_UNDONE, "USER", targetUserId,
            serializeNew(), serializeOld(), null, LocalDateTime.now()
        ));
    }

    @Override
    public boolean isUndoable() { return executed && successful; }

    @Override
    public String getDescription() {
        return String.format("Admin update %s.%s for user %d (%s → %s)",
            targetUserType, fieldName, targetUserId, oldValue, newValue);
    }

    // -------------------------------------------------------------------------
    // Field read / write  (static so MacroCommand can reuse without an instance)
    // -------------------------------------------------------------------------

    /** Returns the current value of fieldName on user as a String. */
    public static String readField(User user, String fieldName) {
        return switch (fieldName) {
            case "firstName"       -> Objects.toString(user.getFirstName(), "");
            case "lastName"        -> Objects.toString(user.getLastName(), "");
            case "email"           -> Objects.toString(user.getEmail(), "");
            case "phone"           -> Objects.toString(user.getPhone(), "");
            case "username"        -> Objects.toString(user.getUsername(), "");
            // Student fields
            case "gpa"             -> user instanceof Student s && s.getGpa() != null
                                          ? s.getGpa().toPlainString() : "0.0";
            case "major"           -> user instanceof Student s ? Objects.toString(s.getMajor(), "") : "";
            case "minor"           -> user instanceof Student s ? Objects.toString(s.getMinor(), "") : "";
            case "classification"  -> user instanceof Student s ? Objects.toString(s.getClassification(), "") : "";
            case "academicStanding"-> user instanceof Student s ? Objects.toString(s.getAcademicStanding(), "") : "";
            case "enrollmentStatus"-> user instanceof Student s ? Objects.toString(s.getEnrollmentStatus(), "") : "";
            case "creditsEarned"   -> user instanceof Student s ? String.valueOf(s.getCreditsEarned()) : "0";
            // Faculty fields
            case "department"      -> user instanceof Faculty f ? Objects.toString(f.getDepartment(), "") : "";
            case "title"           -> user instanceof Faculty f ? Objects.toString(f.getTitle(), "") : "";
            case "officeLocation"  -> user instanceof Faculty f ? Objects.toString(f.getOfficeLocation(), "") : "";
            case "officeHours"     -> user instanceof Faculty f ? Objects.toString(f.getOfficeHours(), "") : "";
            // Admin fields
            case "accessLevel"     -> user instanceof Admin   a ? Objects.toString(a.getAccessLevel(), "") : "";
            default -> throw new IllegalArgumentException("Unsupported field: " + fieldName);
        };
    }

    /** Writes value to the named field on user (parses numeric types as needed). */
    public static void applyField(User user, String fieldName, String value) {
        switch (fieldName) {
            case "firstName"        -> user.setFirstName(value);
            case "lastName"         -> user.setLastName(value);
            case "email"            -> user.setEmail(value);
            case "phone"            -> user.setPhone(value);
            case "username"         -> user.setUsername(value);
            // Student fields
            case "gpa"              -> { if (user instanceof Student s) s.setGpa(new BigDecimal(value)); }
            case "major"            -> { if (user instanceof Student s) s.setMajor(value); }
            case "minor"            -> { if (user instanceof Student s) s.setMinor(value); }
            case "classification"   -> { if (user instanceof Student s) s.setClassification(value); }
            case "academicStanding" -> { if (user instanceof Student s) s.setAcademicStanding(value); }
            case "enrollmentStatus" -> { if (user instanceof Student s) s.setEnrollmentStatus(value); }
            case "creditsEarned"    -> { if (user instanceof Student s) s.setCreditsEarned(Integer.parseInt(value)); }
            // Faculty fields
            case "department"       -> { if (user instanceof Faculty f) f.setDepartment(value); }
            case "title"            -> { if (user instanceof Faculty f) f.setTitle(value); }
            case "officeLocation"   -> { if (user instanceof Faculty f) f.setOfficeLocation(value); }
            case "officeHours"      -> { if (user instanceof Faculty f) f.setOfficeHours(value); }
            // Admin fields
            case "accessLevel"      -> { if (user instanceof Admin   a) a.setAccessLevel(value); }
            default -> throw new IllegalArgumentException("Unsupported field: " + fieldName);
        }
    }

    // -------------------------------------------------------------------------
    // Serialization
    // -------------------------------------------------------------------------

    private String serializeOld() {
        return toJson(Map.of("userId", targetUserId, "field", fieldName, "value", oldValue));
    }

    private String serializeNew() {
        return toJson(Map.of("userId", targetUserId, "field", fieldName, "value", newValue));
    }

    private static String toJson(Map<String, ?> map) {
        try { return new ObjectMapper().writeValueAsString(map); }
        catch (JsonProcessingException e) { throw new RuntimeException(e); }
    }

    @Override
    protected String serializeCommandData() {
        Map<String, Object> data = new HashMap<>();
        data.put("targetUserId",   targetUserId);
        data.put("targetUserType", targetUserType);
        data.put("fieldName",      fieldName);
        data.put("newValue",       newValue);
        data.put("oldValue",       oldValue);
        return toJson(data);
    }

    @Override
    protected void deserializeCommandData(String json) {
        if (json == null || json.isBlank()) return;
        try {
            Map<String, Object> data = new ObjectMapper().readValue(json, Map.class);
            this.targetUserId   = (int)    data.get("targetUserId");
            this.targetUserType = (String) data.get("targetUserType");
            this.fieldName      = (String) data.get("fieldName");
            this.newValue       = (String) data.get("newValue");
            this.oldValue       = (String) data.get("oldValue");
            // Reload the live typed user so execute/undo have a real object to mutate.
            this.targetUser = factory.getTypedUser(targetUserId);
        } catch (Exception e) {
            throw new RuntimeException("Failed to deserialize AdminUpdateUserCommand", e);
        }
    }
}
