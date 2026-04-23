package edu.advising.audit;

import java.time.LocalDateTime;

// AuditEvent is a record class, created by the AuditLog and accessible with getters for all the fields

public record AuditEvent(int id, String userId, EventType eventType, String details, String outcome,
                         LocalDateTime timestamp) {
}
