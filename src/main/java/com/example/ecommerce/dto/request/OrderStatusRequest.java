package com.example.ecommerce.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

public record OrderStatusRequest(
        @NotBlank @Pattern(regexp = "paid|shipped|delivered|cancelled") String status
) {}
