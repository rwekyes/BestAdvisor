package edu.advising.audit;

import edu.advising.core.DatabaseManager;
import edu.advising.users.User;

import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.List;

public class AuditLog {

    private static AuditLog instance;

    private DatabaseManager db = DatabaseManager.getInstance();

    private AuditLog() {}

    public static AuditLog getInstance() {
        if (instance == null) {
            instance = new AuditLog();
        }
        return instance;
    }

    public void log(AuditEvent event){
        SystemAuditLog newLog = new SystemAuditLog(
                event.userId(),
                event.eventType().name(),
                event.entityType(),
                event.entityId(),
                event.oldValue(),
                event.newValue(),
                event.ipAddress(),
                event.timestamp()
        );
        try {
            DatabaseManager.getInstance().upsert(newLog);
        } catch (SQLException | IllegalAccessException e) {
            System.err.println("Failed to upsert audit log" + e.getMessage());
        }
    }

    public void logLogin(User user, boolean success){

    }

    public void logPipelineResult(String userId, String pipeline, List<String> passed, String failedAt, String outcome){

    }

    public List<AuditEvent> getEvents(String userId, EventType type, LocalDateTime from, LocalDateTime to){
        return null; // Stub for now
    }
}
