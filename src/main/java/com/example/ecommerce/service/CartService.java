package com.example.ecommerce.service;

import com.example.ecommerce.dto.request.AddCartItemRequest;
import com.example.ecommerce.dto.request.CartItemRequest;
import com.example.ecommerce.dto.request.UpdateCartItemRequest;
import com.example.ecommerce.dto.response.CartResponse;

public interface CartService {
    CartResponse getCart(Long userId);

    CartResponse addItem(Long userId, CartItemRequest request);
    CartResponse addVariantItem(Long userId, AddCartItemRequest request);
    CartResponse updateItem(Long userId, Long cartItemId, UpdateCartItemRequest request);
    void removeItemById(Long userId, Long cartItemId);
    void removeItem(Long userId, Long productId);
}