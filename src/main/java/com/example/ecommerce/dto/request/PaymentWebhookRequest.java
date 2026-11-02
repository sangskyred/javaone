package com.example.ecommerce.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

public record PaymentWebhookRequest(
        @NotBlank String transactionId,
        @NotBlank @Pattern(regexp = "succeeded|failed") String status,
        String signature
) {}
