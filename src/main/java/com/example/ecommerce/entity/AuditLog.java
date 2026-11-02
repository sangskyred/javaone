package com.example.ecommerce.entity;

import lombok.Getter;
import lombok.Setter;
import java.time.OffsetDateTime;
import java.util.Map;

@Getter
@Setter
public class AuditLog {
    private Long id;
    private Long actorUserId;
    private String action;
    private String entityType;
    private Long entityId;
    private Map<String, Object> oldValues;
    private Map<String, Object> newValues;
    private String ipAddress;
    private OffsetDateTime createdAt;
}
