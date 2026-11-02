package com.example.ecommerce.entity;

import lombok.Getter;
import lombok.Setter;
import java.time.OffsetDateTime;

@Getter
@Setter
public class Notification {
    private Long id;
    private Long userId;
    private String type;
    private String title;
    private String body;
    private Boolean read;
    private OffsetDateTime createdAt;
}
