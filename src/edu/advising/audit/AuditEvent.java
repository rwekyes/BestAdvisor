package edu.advising.audit;

import java.time.LocalDateTime;

// AuditEvent is a record class, created by the AuditLog and accessible with getters for all the fields

public record AuditEvent(int id, int userId, EventType eventType, String entityType, int entityId, String oldValue, String newValue, String ipAddress,
                         LocalDateTime timestamp) {

}
