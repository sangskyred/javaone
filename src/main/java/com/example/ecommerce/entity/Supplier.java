package com.example.ecommerce.entity;

import lombok.Getter;
import lombok.Setter;
import java.time.OffsetDateTime;

@Getter
@Setter
public class Supplier {
    private Long id;
    private String name;
    private String contactEmail;
    private String contactPhone;
    private String address;
    private Boolean active;
    private OffsetDateTime createdAt;
}
