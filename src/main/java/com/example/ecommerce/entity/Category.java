package com.example.ecommerce.entity;

import lombok.Getter;
import lombok.Setter;
import java.time.OffsetDateTime;

@Getter
@Setter
public class Category {
    private Long id;
    private String name;
    private String slug;
    private String description;
    private Long parentId;
    private OffsetDateTime createdAt;
}
