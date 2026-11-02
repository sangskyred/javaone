package com.example.ecommerce.dto.request;

import jakarta.validation.constraints.NotBlank;
import java.util.List;

public record ProductRequest(
        Long categoryId,
        Long brandId,
        @NotBlank String name,
        String description,
        List<@jakarta.validation.Valid ProductMediaRequest> media
) {
}