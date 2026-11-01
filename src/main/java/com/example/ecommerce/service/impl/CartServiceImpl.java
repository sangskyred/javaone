package com.example.ecommerce.service.impl;

import com.example.ecommerce.dto.request.CartItemRequest;
import com.example.ecommerce.dto.response.CartResponse;
import com.example.ecommerce.entity.Product;
import com.example.ecommerce.exception.InsufficientStockException;
import com.example.ecommerce.exception.ResourceNotFoundException;
import com.example.ecommerce.mapper.CartMapper;
import com.example.ecommerce.repository.CartRepository;
import com.example.ecommerce.repository.ProductRepository;
import com.example.ecommerce.service.CartService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
public class CartServiceImpl implements CartService {
    private final CartRepository cartRepository;
    private final ProductRepository productRepository;
    private final CartMapper cartMapper;

    public CartServiceImpl(CartRepository cartRepository, ProductRepository productRepository, CartMapper cartMapper) {
        this.cartRepository = cartRepository;
        this.productRepository = productRepository;
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

        Integer currentQuantity = cartRepository.findQuantity(userId, request.productId());
        int updatedQuantity = request.quantity() + (currentQuantity == null ? 0 : currentQuantity);
        if (updatedQuantity > product.getStock()) {
            throw new InsufficientStockException("Requested quantity exceeds available stock");
        }
        if (currentQuantity == null) {
            cartRepository.insert(userId, request.productId(), updatedQuantity);
        } else {
            cartRepository.updateQuantity(userId, request.productId(), updatedQuantity);
        }
        return getCart(userId);
    }

    @Override
    @Transactional
    public void removeItem(Long userId, Long productId) {
        cartRepository.deleteItem(userId, productId);
    }
}