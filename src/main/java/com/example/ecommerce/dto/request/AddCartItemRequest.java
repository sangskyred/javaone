package com.example.ecommerce.dto.request;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

public record AddCartItemRequest(@NotNull Long variantId, @NotNull @Positive Integer quantity) {}
