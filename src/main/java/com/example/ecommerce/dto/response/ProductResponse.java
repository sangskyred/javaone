package com.example.ecommerce.dto.response;

import java.math.BigDecimal;

import java.util.List;

public record ProductResponse(Long id, String name, String description, BigDecimal price, Integer stock,
                              List<ProductMediaResponse> media) {
}