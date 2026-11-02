package com.example.ecommerce.entity;

import lombok.Getter;
import lombok.Setter;
import java.time.OffsetDateTime;

@Getter
@Setter
public class Review {
    private Long id;
    private Long productId;
    private Long userId;
    private Long orderItemId;
    private Short rating;
    private String title;
    private String content;
    private Boolean approved;
    private OffsetDateTime createdAt;
}
