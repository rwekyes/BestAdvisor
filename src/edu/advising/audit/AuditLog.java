package edu.advising.audit;

import edu.advising.core.DatabaseManager;
import edu.advising.users.User;

import java.time.LocalDateTime;
import java.util.List;

public class AuditLog {

    private static AuditLog instance;

    private DatabaseManager db = DatabaseManager.getInstance();

    public static AuditLog getInstance(){return instance;}

    public void log(AuditEvent event){

    }

    public void logLogin(User user, boolean success){

    }

    public void logPipelineResult(String userId, String pipeline, List<String> passed, String failedAt, String outcome){

    }

    public List<AuditEvent> getEvents(String userId, EventType type, LocalDateTime from, LocalDateTime to){
        return null; // Stub for now
    }
}
