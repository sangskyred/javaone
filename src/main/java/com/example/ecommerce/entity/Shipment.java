package com.example.ecommerce.entity;

import lombok.Getter;
import lombok.Setter;
import java.time.OffsetDateTime;

@Getter
@Setter
public class Shipment {
    private Long id;
    private Long orderId;
    private Long addressId;
    private String carrier;
    private String trackingNumber;
    private String status;
    private OffsetDateTime shippedAt;
    private OffsetDateTime deliveredAt;
    private OffsetDateTime createdAt;
}
