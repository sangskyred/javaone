package com.example.ecommerce.entity;

import lombok.Getter;
import lombok.Setter;
import java.time.OffsetDateTime;

@Getter
@Setter
public class Brand {
    private Long id;
    private String name;
    private String slug;
    private String logoUrl;
    private String description;
    private String country;
    private Boolean active;
    private OffsetDateTime createdAt;
}
