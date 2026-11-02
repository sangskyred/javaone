package com.example.ecommerce.dto.response;

import java.math.BigDecimal;
import java.util.Map;
import java.util.List;

public record VariantResponse(Long id, String sku, String name, Map<String, Object> attributes,
                              BigDecimal price, Integer stock, boolean isActive,
                              List<ProductMediaResponse> media) {}
