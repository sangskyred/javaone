package com.example.ecommerce.entity;

import lombok.Getter;
import lombok.Setter;
import java.time.OffsetDateTime;

@Getter
@Setter
public class Address {
    private Long id;
    private Long userId;
    private String recipientName;
    private String phone;
    private String line1;
    private String line2;
    private String city;
    private String postalCode;
    private String countryCode;
    private Boolean defaultAddress;
    private OffsetDateTime createdAt;
}
