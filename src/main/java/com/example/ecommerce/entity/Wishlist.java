package com.example.ecommerce.entity;

import lombok.Getter;
import lombok.Setter;
import java.time.OffsetDateTime;

@Getter
@Setter
public class Wishlist {
    private Long userId;
    private Long productId;
    private OffsetDateTime createdAt;
}
