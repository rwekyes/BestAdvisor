package edu.advising.commands;

// ============================================================================
// ADMIN USER MANAGEMENT — AdminSetUserActiveCommand (Concrete Command)
// ============================================================================
//
// Activates or deactivates a user account.  Undo always restores the previous
// state, so it works symmetrically whether you're deactivating or reactivating.
//
// GUI WIRING:
//   // Deactivate selected user:
//   executor.execute(new AdminSetUserActiveCommand(selectedUser, false));
//   // Reactivate:
//   executor.execute(new AdminSetUserActiveCommand(selectedUser, true));
//
// ============================================================================

import com.fasterxml.jackson.core.JsonProcessingException;
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
import java.util.HashMap;
import java.util.Map;

@Table(name = "command_history", isSubTable = true)
public class AdminSetUserActiveCommand extends BaseCommand {

    private User    targetUser;
    private boolean targetActive;   // the desired new state
    private boolean previousActive; // snapshot for undo
    private int     targetUserId;

    private final DatabaseManager dbManager = DatabaseManager.getInstance();
    private final UserFactory     factory   = new UserFactory();

    public AdminSetUserActiveCommand() {
        this(new User(), false);
    }

    public AdminSetUserActiveCommand(User targetUser, boolean targetActive) {
        super();
        this.commandType    = targetActive ? "ADMIN_REACTIVATE_USER" : "ADMIN_DEACTIVATE_USER";
        this.targetUser     = targetUser;
        this.targetActive   = targetActive;
        this.previousActive = Boolean.TRUE.equals(targetUser.isActive());
        this.targetUserId   = targetUser.getId();
    }

    public static AdminSetUserActiveCommand fromSuperType(BaseCommand base) {
        AdminSetUserActiveCommand cmd = new AdminSetUserActiveCommand();
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
            setActiveInDb(targetActive);
            targetUser.setActive(targetActive);
            executed   = true;
            successful = true;
            System.out.printf("✓ User %d %s%n", targetUserId,
                targetActive ? "reactivated" : "deactivated");
        } catch (SQLException e) {
            successful   = false;
            errorMessage = "Failed to update active status: " + e.getMessage();
            System.err.println("✗ " + errorMessage);
        }

        AuditLog.getInstance().log(new AuditEvent(
            0, userId, EventType.COMMAND_EXECUTED, "USER", targetUserId,
            toJson(previousActive), toJson(targetActive), null, LocalDateTime.now()
        ));
    }

    @Override
    public void undo() {
        if (!executed || !successful) {
            System.out.println("Cannot undo — status change was not completed.");
            return;
        }
        try {
            setActiveInDb(previousActive);
            targetUser.setActive(previousActive);
            this.undoneAt = LocalDateTime.now();
            this.isUndone = true;
            System.out.printf("↶ Restored user %d active status to %b%n",
                targetUserId, previousActive);
        } catch (SQLException e) {
            System.err.println("✗ Undo failed: " + e.getMessage());
        }

        AuditLog.getInstance().log(new AuditEvent(
            0, userId, EventType.COMMAND_UNDONE, "USER", targetUserId,
            toJson(targetActive), toJson(previousActive), null, LocalDateTime.now()
        ));
    }

    @Override
    public boolean isUndoable() { return executed && successful; }

    @Override
    public String getDescription() {
        return String.format("Admin %s user %d",
            targetActive ? "reactivate" : "deactivate", targetUserId);
    }

    // -------------------------------------------------------------------------
    // Helpers
    // -------------------------------------------------------------------------

    private void setActiveInDb(boolean active) throws SQLException {
        dbManager.executeUpdate(
            "UPDATE users SET is_active = ?, updated_at = ? WHERE id = ?",
            active, LocalDateTime.now(), targetUserId);
    }

    private static String toJson(boolean active) {
        try { return new ObjectMapper().writeValueAsString(Map.of("isActive", active)); }
        catch (JsonProcessingException e) { throw new RuntimeException(e); }
    }

    // -------------------------------------------------------------------------
    // Serialization
    // -------------------------------------------------------------------------

    @Override
    protected String serializeCommandData() {
        Map<String, Object> data = new HashMap<>();
        data.put("targetUserId",   targetUserId);
        data.put("targetActive",   targetActive);
        data.put("previousActive", previousActive);
        try { return new ObjectMapper().writeValueAsString(data); }
        catch (JsonProcessingException e) { throw new RuntimeException(e); }
    }

    @Override
    protected void deserializeCommandData(String json) {
        if (json == null || json.isBlank()) return;
        try {
            Map<String, Object> data = new ObjectMapper().readValue(json, Map.class);
            this.targetUserId   = (int)     data.get("targetUserId");
            this.targetActive   = (boolean) data.get("targetActive");
            this.previousActive = (boolean) data.get("previousActive");
            this.targetUser = factory.getTypedUser(targetUserId);
        } catch (Exception e) {
            throw new RuntimeException("Failed to deserialize AdminSetUserActiveCommand", e);
        }
    }
}
