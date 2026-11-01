package com.example.ecommerce.dto.response;

import java.math.BigDecimal;

public record CartItemResponse(Long productId, String productName, BigDecimal unitPrice,
                               Integer quantity, BigDecimal lineTotal) {
}