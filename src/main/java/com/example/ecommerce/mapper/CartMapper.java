package com.example.ecommerce.mapper;

import com.example.ecommerce.dto.response.CartItemResponse;
import com.example.ecommerce.dto.response.CartResponse;
import com.example.ecommerce.entity.CartLine;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.List;

@Component
public class CartMapper {
    public CartResponse toResponse(List<CartLine> lines) {
        List<CartItemResponse> items = lines.stream()
                .map(line -> new CartItemResponse(line.getProductId(), line.getProductName(), line.getUnitPrice(),
                        line.getQuantity(), line.getLineTotal()))
                .toList();
        BigDecimal total = lines.stream()
                .map(CartLine::getLineTotal)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        return new CartResponse(items, total);
    }
}