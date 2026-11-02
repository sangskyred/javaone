package com.example.ecommerce.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record ProductMediaRequest(
        Long variantId,
        @NotBlank @Pattern(regexp = "image|video") String mediaType,
        @NotBlank @Size(max = 500) String url,
        @Size(max = 500) String thumbnailUrl,
        Integer sortOrder,
        Boolean isMain
) {}
