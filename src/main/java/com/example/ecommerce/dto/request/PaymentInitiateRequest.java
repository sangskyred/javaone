package com.example.ecommerce.dto.request;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;

public record PaymentInitiateRequest(
        @NotNull Long orderId,
        @Pattern(regexp = "cod|bank_transfer|momo|vnpay|paypal|stripe") String method
) {}
