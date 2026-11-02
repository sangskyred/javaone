package com.example.ecommerce.controller;

import com.example.ecommerce.dto.ApiResponse;
import com.example.ecommerce.dto.request.CheckoutRequest;
import com.example.ecommerce.dto.response.CheckoutPreviewResponse;
import com.example.ecommerce.dto.response.OrderResponse;
import com.example.ecommerce.dto.response.PageResponse;
import com.example.ecommerce.security.EcommerceUserDetails;
import com.example.ecommerce.service.OrderService;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping({"/api/orders", "/api/v1/orders"})
public class OrderController {
    private final OrderService orderService;

    public OrderController(OrderService orderService) {
        this.orderService = orderService;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ApiResponse<OrderResponse> checkout(@AuthenticationPrincipal EcommerceUserDetails user,
                                                @RequestBody(required = false) CheckoutRequest request) {
        String couponCode = request == null ? null : request.couponCode();
        return ApiResponse.success(HttpStatus.CREATED.value(), "Order created",
                orderService.checkout(user.getAccount().getId(), couponCode));
    }

    @PostMapping("/checkout-preview")
    public ApiResponse<CheckoutPreviewResponse> checkoutPreview(@AuthenticationPrincipal EcommerceUserDetails user,
                                                                 @RequestBody(required = false) CheckoutRequest request) {
        String couponCode = request == null ? null : request.couponCode();
        return ApiResponse.success(orderService.checkoutPreview(user.getAccount().getId(), couponCode));
    }

    @GetMapping({"", "/my-orders"})
    public ApiResponse<?> findMine(@AuthenticationPrincipal EcommerceUserDetails user,
                                   @RequestParam(required = false) String status,
                                   @RequestParam(defaultValue = "0") int page,
                                   @RequestParam(defaultValue = "20") int size) {
        if (page > 0 || size != 20 || status != null) {
            return ApiResponse.success(orderService.findMyOrders(user.getAccount().getId(), status, page, size));
        }
        return ApiResponse.success(orderService.findMyOrders(user.getAccount().getId(), null, 0, 20));
    }

    @PostMapping("/{id}/cancel")
    public ApiResponse<OrderResponse> cancel(@AuthenticationPrincipal EcommerceUserDetails user, @PathVariable Long id) {
        return ApiResponse.success(orderService.cancel(user.getAccount().getId(), id));
    }

    @GetMapping("/legacy")
    public ApiResponse<List<OrderResponse>> findMineLegacy(@AuthenticationPrincipal EcommerceUserDetails user) {
        return ApiResponse.success(orderService.findByUserId(user.getAccount().getId()));
    }

    @GetMapping("/{id}")
    public ApiResponse<OrderResponse> findById(@AuthenticationPrincipal EcommerceUserDetails user, @PathVariable Long id) {
        return ApiResponse.success(orderService.findById(user.getAccount().getId(), id));
    }
}