package com.example.ecommerce.mapper;

import com.example.ecommerce.dto.response.OrderItemResponse;
import com.example.ecommerce.dto.response.OrderResponse;
import com.example.ecommerce.entity.OrderLine;
import com.example.ecommerce.entity.PurchaseOrder;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class OrderMapper {
    public OrderResponse toResponse(PurchaseOrder order, List<OrderLine> lines) {
        List<OrderItemResponse> items = lines.stream()
                .map(line -> new OrderItemResponse(line.getVariantId(), line.getProductName(),
                        line.getUnitPrice(), line.getQuantity()))
                .toList();
        return new OrderResponse(order.getId(), order.getTotal(), order.getStatus(), order.getCreatedAt(), items);
    }
}