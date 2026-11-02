package com.example.ecommerce.dto.response;

public record ProductMediaResponse(Long id, Long variantId, String mediaType, String url,
                                   String thumbnailUrl, Integer sortOrder, boolean isMain) {}
