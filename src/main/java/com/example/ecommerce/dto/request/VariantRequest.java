package com.example.ecommerce.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

public record VariantRequest(
        @NotBlank String sku,
        String name,
        Map<String, Object> attributes,
        @NotNull @PositiveOrZero BigDecimal price,
        @NotNull @PositiveOrZero Integer stock,
        boolean isActive,
        List<@jakarta.validation.Valid ProductMediaRequest> media
) {}
