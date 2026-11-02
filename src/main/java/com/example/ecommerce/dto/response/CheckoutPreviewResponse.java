package com.example.ecommerce.dto.response;

import java.math.BigDecimal;

public record CheckoutPreviewResponse(
        BigDecimal subtotal,
        BigDecimal discountAmount,
        BigDecimal shippingFee,
        BigDecimal total
) {}
