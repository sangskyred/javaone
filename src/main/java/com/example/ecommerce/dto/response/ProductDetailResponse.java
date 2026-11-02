package com.example.ecommerce.dto.response;

import java.util.List;

public record ProductDetailResponse(Long id, String name, String description,
                                    List<VariantResponse> variants) {}
