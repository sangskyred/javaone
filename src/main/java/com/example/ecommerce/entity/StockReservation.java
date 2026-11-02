package com.example.ecommerce.entity;

import lombok.Getter;
import lombok.Setter;
import java.time.OffsetDateTime;

@Getter
@Setter
public class StockReservation {
    private Long id;
    private Long orderId;
    private Long variantId;
    private Integer quantity;
    private OffsetDateTime expiresAt;
    private OffsetDateTime releasedAt;
    private OffsetDateTime createdAt;
}
