package com.example.ecommerce.entity;

import lombok.Getter;
import lombok.Setter;
import java.math.BigDecimal;
import java.time.OffsetDateTime;

@Getter
@Setter
public class CouponRedemption {
    private Long id;
    private Long couponId;
    private Long userId;
    private Long orderId;
    private BigDecimal discountAmount;
    private OffsetDateTime createdAt;
}
