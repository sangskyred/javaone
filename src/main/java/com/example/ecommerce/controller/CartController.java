package com.example.ecommerce.controller;

import com.example.ecommerce.dto.request.CartItemRequest;
import com.example.ecommerce.dto.response.CartResponse;
import com.example.ecommerce.security.EcommerceUserDetails;
import com.example.ecommerce.service.CartService;
import jakarta.validation.Valid;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.http.HttpStatus;

@RestController
@RequestMapping("/api/cart")
public class CartController {
    private final CartService cartService;

    public CartController(CartService cartService) {
        this.cartService = cartService;
    }

    @GetMapping
    public CartResponse getCart(@AuthenticationPrincipal EcommerceUserDetails user) {
        return cartService.getCart(user.getAccount().getId());
    }

    @PostMapping("/items")
    public CartResponse addItem(@AuthenticationPrincipal EcommerceUserDetails user,
                                @Valid @RequestBody CartItemRequest request) {
        return cartService.addItem(user.getAccount().getId(), request);
    }

    @DeleteMapping("/items/{productId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void removeItem(@AuthenticationPrincipal EcommerceUserDetails user, @PathVariable Long productId) {
        cartService.removeItem(user.getAccount().getId(), productId);
    }
}