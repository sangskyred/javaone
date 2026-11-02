package com.example.ecommerce.entity;

import lombok.Getter;
import lombok.Setter;
import java.math.BigDecimal;
import java.time.OffsetDateTime;

@Getter
@Setter
public class Coupon {
    private Long id;
    private String code;
    private String discountType;
    private BigDecimal discountValue;
    private Integer maxUses;
    private Integer usedCount;
    private BigDecimal minOrderAmount;
    private OffsetDateTime startsAt;
    private OffsetDateTime expiresAt;
    private Boolean active;
    private OffsetDateTime createdAt;
}
