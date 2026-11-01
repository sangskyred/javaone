package com.example.ecommerce.service;

import com.example.ecommerce.dto.response.OrderResponse;

import java.util.List;

public interface OrderService {
    OrderResponse checkout(Long userId);

    List<OrderResponse> findByUserId(Long userId);

    OrderResponse findById(Long userId, Long orderId);
}