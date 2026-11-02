package com.example.ecommerce.controller;

import com.example.ecommerce.dto.ApiResponse;
import com.example.ecommerce.dto.request.AddCartItemRequest;
import com.example.ecommerce.dto.request.CartItemRequest;
import com.example.ecommerce.dto.request.UpdateCartItemRequest;
import com.example.ecommerce.dto.response.CartResponse;
import com.example.ecommerce.security.EcommerceUserDetails;
import com.example.ecommerce.service.CartService;
import jakarta.validation.Valid;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.http.HttpStatus;

@RestController
@RequestMapping({"/api/cart", "/api/v1/cart"})
public class CartController {
    private final CartService cartService;

    public CartController(CartService cartService) {
        this.cartService = cartService;
    }

    @GetMapping
    public ApiResponse<CartResponse> getCart(@AuthenticationPrincipal EcommerceUserDetails user) {
        return ApiResponse.success(cartService.getCart(user.getAccount().getId()));
    }

    @PostMapping("/add")
    public ApiResponse<CartResponse> addVariantItem(@AuthenticationPrincipal EcommerceUserDetails user,
                                                     @Valid @RequestBody AddCartItemRequest request) {
        return ApiResponse.success(cartService.addVariantItem(user.getAccount().getId(), request));
    }

    @PostMapping("/items")
    public ApiResponse<CartResponse> addItem(@AuthenticationPrincipal EcommerceUserDetails user,
                                             @Valid @RequestBody CartItemRequest request) {
        return ApiResponse.success(cartService.addItem(user.getAccount().getId(), request));
    }

    @PutMapping("/items/{cartItemId}")
    public ApiResponse<CartResponse> updateItem(@AuthenticationPrincipal EcommerceUserDetails user,
                                                @PathVariable Long cartItemId,
                                                @Valid @RequestBody UpdateCartItemRequest request) {
        return ApiResponse.success(cartService.updateItem(user.getAccount().getId(), cartItemId, request));
    }

    @DeleteMapping("/items/{cartItemId}")
    public ApiResponse<Void> removeItemById(@AuthenticationPrincipal EcommerceUserDetails user,
                                             @PathVariable Long cartItemId) {
        cartService.removeItemById(user.getAccount().getId(), cartItemId);
        return ApiResponse.success(200, "Cart item removed", null);
    }

    @DeleteMapping("/items/product/{productId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void removeItem(@AuthenticationPrincipal EcommerceUserDetails user, @PathVariable Long productId) {
        cartService.removeItem(user.getAccount().getId(), productId);
    }
}