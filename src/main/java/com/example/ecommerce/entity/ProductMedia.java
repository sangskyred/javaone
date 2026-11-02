package com.example.ecommerce.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.OffsetDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProductMedia {
    private Long id;
    private Long productId;
    private Long variantId;
    private String mediaType;
    private String url;
    private String thumbnailUrl;
    private Integer sortOrder;
    private Boolean main;
    private OffsetDateTime createdAt;
}
