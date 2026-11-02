package com.example.ecommerce.entity;

import lombok.Getter;
import lombok.Setter;
import java.time.OffsetDateTime;

@Getter
@Setter
public class StockMovement {
    private Long id;
    private Long variantId;
    private Integer changeAmount;
    private String reason;
    private Long referenceId;
    private String note;
    private OffsetDateTime createdAt;
}
