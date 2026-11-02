package com.example.ecommerce.dto.response;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.List;

public record OrderResponse(Long id, BigDecimal total, String status, OffsetDateTime createdAt,
                            List<OrderItemResponse> items) {
}