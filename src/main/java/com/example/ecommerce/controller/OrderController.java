package com.example.ecommerce.controller;

import com.example.ecommerce.dto.response.OrderResponse;
import com.example.ecommerce.security.EcommerceUserDetails;
import com.example.ecommerce.service.OrderService;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/orders")
public class OrderController {
    private final OrderService orderService;

    public OrderController(OrderService orderService) {
        this.orderService = orderService;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public OrderResponse checkout(@AuthenticationPrincipal EcommerceUserDetails user) {
        return orderService.checkout(user.getAccount().getId());
    }

    @GetMapping
    public List<OrderResponse> findMine(@AuthenticationPrincipal EcommerceUserDetails user) {
        return orderService.findByUserId(user.getAccount().getId());
    }

    @GetMapping("/{id}")
    public OrderResponse findById(@AuthenticationPrincipal EcommerceUserDetails user, @PathVariable Long id) {
        return orderService.findById(user.getAccount().getId(), id);
    }
}