package com.example.ecommerce.service;

import com.example.ecommerce.dto.response.CheckoutPreviewResponse;
import com.example.ecommerce.dto.response.OrderResponse;
import com.example.ecommerce.dto.response.PageResponse;

import java.util.List;

public interface OrderService {
    OrderResponse checkout(Long userId);
    OrderResponse checkout(Long userId, String couponCode);
    CheckoutPreviewResponse checkoutPreview(Long userId);
    CheckoutPreviewResponse checkoutPreview(Long userId, String couponCode);
    PageResponse<OrderResponse> findMyOrders(Long userId, String status, int page, int size);
    OrderResponse cancel(Long userId, Long orderId);
    OrderResponse updateStatus(Long orderId, String status);

    List<OrderResponse> findByUserId(Long userId);

    OrderResponse findById(Long userId, Long orderId);
}