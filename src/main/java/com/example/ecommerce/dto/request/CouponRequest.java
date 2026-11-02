package com.example.ecommerce.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;
import java.math.BigDecimal;
import java.time.OffsetDateTime;

public record CouponRequest(
        @NotBlank String code,
        @NotBlank @Pattern(regexp = "percent|fixed") String discountType,
        @NotNull @Positive BigDecimal discountValue,
        Integer maxUses,
        @NotNull @PositiveOrZero BigDecimal minOrderAmount,
        @NotNull OffsetDateTime startsAt,
        OffsetDateTime expiresAt,
        boolean isActive
) {}
