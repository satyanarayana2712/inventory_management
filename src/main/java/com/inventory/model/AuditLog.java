package com.inventory.model;

import java.time.LocalDateTime;

/** Asynchronous audit log entry. */
public record AuditLog(int logId, String user, String action, LocalDateTime timestamp) {
}
