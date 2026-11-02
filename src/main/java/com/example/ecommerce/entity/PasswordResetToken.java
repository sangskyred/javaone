package com.example.ecommerce.entity;

import lombok.Getter;
import lombok.Setter;
import java.time.OffsetDateTime;

@Getter
@Setter
public class PasswordResetToken {
    private Long id;
    private Long userId;
    private String tokenHash;
    private OffsetDateTime expiresAt;
    private OffsetDateTime usedAt;
    private OffsetDateTime createdAt;
}
