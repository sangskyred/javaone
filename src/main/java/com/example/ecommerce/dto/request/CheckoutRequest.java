package com.example.ecommerce.dto.request;

import jakarta.validation.constraints.Size;

public record CheckoutRequest(Long shippingAddressId, @Size(max = 50) String couponCode, String currency) {}
