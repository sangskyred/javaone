package com.example.ecommerce.entity;

import lombok.Getter;
import lombok.Setter;
import java.math.BigDecimal;
import java.time.OffsetDateTime;

@Getter
@Setter
public class Payment {
    private Long id;
    private Long orderId;
    private BigDecimal amount;
    private String currency;
    private String method;
    private String status;
    private String gatewayTransactionId;
    private OffsetDateTime paidAt;
    private OffsetDateTime refundedAt;
    private OffsetDateTime createdAt;
}
