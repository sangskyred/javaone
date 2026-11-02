package com.example.ecommerce.service.impl;

import com.example.ecommerce.dto.request.AddCartItemRequest;
import com.example.ecommerce.dto.request.CartItemRequest;
import com.example.ecommerce.dto.request.UpdateCartItemRequest;
import com.example.ecommerce.dto.response.CartResponse;
import com.example.ecommerce.entity.CartLine;
import com.example.ecommerce.entity.Product;
import com.example.ecommerce.entity.ProductVariant;
import com.example.ecommerce.exception.InsufficientStockException;
import com.example.ecommerce.exception.ResourceNotFoundException;
import com.example.ecommerce.mapper.CartMapper;
import com.example.ecommerce.repository.CartRepository;
import com.example.ecommerce.repository.ProductRepository;
import com.example.ecommerce.repository.ProductVariantRepository;
import com.example.ecommerce.service.CartService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
public class CartServiceImpl implements CartService {
    private final CartRepository cartRepository;
    private final ProductRepository productRepository;
    private final ProductVariantRepository productVariantRepository;
    private final CartMapper cartMapper;

    public CartServiceImpl(CartRepository cartRepository, ProductRepository productRepository,
                           ProductVariantRepository productVariantRepository, CartMapper cartMapper) {
        this.cartRepository = cartRepository;
        this.productRepository = productRepository;
        this.productVariantRepository = productVariantRepository;
        this.cartMapper = cartMapper;
    }

    @Override
    public CartResponse getCart(Long userId) {
        return cartMapper.toResponse(cartRepository.findByUserId(userId));
    }

    @Override
    @Transactional
    public CartResponse addItem(Long userId, CartItemRequest request) {
        Product product = productRepository.findById(request.productId());
        if (product == null) {
            throw new ResourceNotFoundException("Product " + request.productId() + " was not found");
        }

        ProductVariant variant = productVariantRepository.findDefaultByProductId(request.productId());
        if (variant == null) {
            throw new ResourceNotFoundException("Active variant for product " + request.productId() + " was not found");
        }
        Integer currentQuantity = cartRepository.findQuantity(userId, variant.getId());
        int updatedQuantity = request.quantity() + (currentQuantity == null ? 0 : currentQuantity);
        if (updatedQuantity > variant.getStock()) {
            throw new InsufficientStockException("Requested quantity exceeds available stock");
        }
        if (currentQuantity == null) {
            cartRepository.insert(userId, variant.getId(), updatedQuantity);
        } else {
            cartRepository.updateQuantity(userId, variant.getId(), updatedQuantity);
        }
        return getCart(userId);
    }

    @Override
    @Transactional
    public CartResponse addVariantItem(Long userId, AddCartItemRequest request) {
        ProductVariant variant = productVariantRepository.findById(request.variantId());
        if (variant == null || Boolean.FALSE.equals(variant.getActive())) {
            throw new ResourceNotFoundException("Variant " + request.variantId() + " was not found or inactive");
        }
        Integer currentQuantity = cartRepository.findQuantity(userId, variant.getId());
        int updatedQuantity = request.quantity() + (currentQuantity == null ? 0 : currentQuantity);
        if (updatedQuantity > variant.getStock()) {
            throw new InsufficientStockException("Requested quantity exceeds available stock");
        }
        if (currentQuantity == null) {
            cartRepository.insert(userId, variant.getId(), updatedQuantity);
        } else {
            cartRepository.updateQuantity(userId, variant.getId(), updatedQuantity);
        }
        return getCart(userId);
    }

    @Override
    @Transactional
    public CartResponse updateItem(Long userId, Long cartItemId, UpdateCartItemRequest request) {
        CartLine item = cartRepository.findByIdAndUserId(cartItemId, userId);
        if (item == null) {
            throw new ResourceNotFoundException("Cart item " + cartItemId + " was not found");
        }
        ProductVariant variant = productVariantRepository.findById(item.getVariantId());
        if (variant == null || request.quantity() > variant.getStock()) {
            throw new InsufficientStockException("Requested quantity exceeds available stock");
        }
        if (cartRepository.updateQuantityById(cartItemId, userId, request.quantity()) == 0) {
            throw new ResourceNotFoundException("Cart item " + cartItemId + " was not found");
        }
        return getCart(userId);
    }

    @Override
    @Transactional
    public void removeItemById(Long userId, Long cartItemId) {
        if (cartRepository.deleteByIdAndUserId(cartItemId, userId) == 0) {
            throw new ResourceNotFoundException("Cart item " + cartItemId + " was not found");
        }
    }

    @Override
    @Transactional
    public void removeItem(Long userId, Long productId) {
        cartRepository.deleteItem(userId, productId);
    }
}