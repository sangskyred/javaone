package com.example.ecommerce.service.impl;

import com.example.ecommerce.dto.response.OrderResponse;
import com.example.ecommerce.entity.CartLine;
import com.example.ecommerce.entity.PurchaseOrder;
import com.example.ecommerce.exception.BadRequestException;
import com.example.ecommerce.exception.InsufficientStockException;
import com.example.ecommerce.exception.ResourceNotFoundException;
import com.example.ecommerce.mapper.OrderMapper;
import com.example.ecommerce.repository.CartRepository;
import com.example.ecommerce.repository.OrderRepository;
import com.example.ecommerce.repository.ProductRepository;
import com.example.ecommerce.service.OrderService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;

@Service
@Transactional(readOnly = true)
public class OrderServiceImpl implements OrderService {
    private final CartRepository cartRepository;
    private final OrderRepository orderRepository;
    private final ProductRepository productRepository;
    private final OrderMapper orderMapper;

    public OrderServiceImpl(CartRepository cartRepository, OrderRepository orderRepository,
                            ProductRepository productRepository, OrderMapper orderMapper) {
        this.cartRepository = cartRepository;
        this.orderRepository = orderRepository;
        this.productRepository = productRepository;
        this.orderMapper = orderMapper;
    }

    @Override
    @Transactional
    public OrderResponse checkout(Long userId) {
        List<CartLine> cartLines = cartRepository.findByUserId(userId);
        if (cartLines.isEmpty()) {
            throw new BadRequestException("Cannot create an order from an empty cart");
        }

        BigDecimal total = cartLines.stream()
                .map(CartLine::getLineTotal)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        PurchaseOrder order = new PurchaseOrder();
        order.setUserId(userId);
        order.setTotal(total);
        order.setStatus("PLACED");
        orderRepository.insert(order);

        for (CartLine line : cartLines) {
            if (productRepository.decrementStock(line.getProductId(), line.getQuantity()) == 0) {
                throw new InsufficientStockException("Insufficient stock for product " + line.getProductId());
            }
            orderRepository.insertLine(order.getId(), line.getProductId(), line.getProductName(),
                    line.getUnitPrice(), line.getQuantity());
        }
        cartRepository.clear(userId);
        return findById(userId, order.getId());
    }

    @Override
    public List<OrderResponse> findByUserId(Long userId) {
        return orderRepository.findByUserId(userId).stream().map(this::toResponse).toList();
    }

    @Override
    public OrderResponse findById(Long userId, Long orderId) {
        PurchaseOrder order = orderRepository.findByIdAndUserId(orderId, userId);
        if (order == null) {
            throw new ResourceNotFoundException("Order " + orderId + " was not found");
        }
        return toResponse(order);
    }

    private OrderResponse toResponse(PurchaseOrder order) {
        return orderMapper.toResponse(order, orderRepository.findLines(order.getId()));
    }
}