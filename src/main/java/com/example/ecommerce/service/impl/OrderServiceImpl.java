package com.example.ecommerce.service.impl;

import com.example.ecommerce.dto.response.CheckoutPreviewResponse;
import com.example.ecommerce.dto.response.OrderResponse;
import com.example.ecommerce.dto.response.PageResponse;
import com.example.ecommerce.entity.CartLine;
import com.example.ecommerce.entity.OrderLine;
import com.example.ecommerce.entity.PurchaseOrder;
import com.example.ecommerce.entity.StockMovement;
import com.example.ecommerce.entity.Coupon;
import com.example.ecommerce.entity.CouponRedemption;
import com.example.ecommerce.repository.CouponRepository;
import com.example.ecommerce.repository.CouponRedemptionRepository;
import com.example.ecommerce.service.CouponService;
import com.example.ecommerce.exception.BadRequestException;
import com.example.ecommerce.exception.InsufficientStockException;
import com.example.ecommerce.exception.ResourceNotFoundException;
import com.example.ecommerce.mapper.OrderMapper;
import com.example.ecommerce.repository.CartRepository;
import com.example.ecommerce.repository.OrderRepository;
import com.example.ecommerce.repository.ProductVariantRepository;
import com.example.ecommerce.repository.StockMovementRepository;
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
    private final ProductVariantRepository productVariantRepository;
    private final StockMovementRepository stockMovementRepository;
    private final CouponService couponService;
    private final CouponRepository couponRepository;
    private final CouponRedemptionRepository couponRedemptionRepository;
    private final OrderMapper orderMapper;

    public OrderServiceImpl(CartRepository cartRepository, OrderRepository orderRepository,
                            ProductVariantRepository productVariantRepository,
                            StockMovementRepository stockMovementRepository, CouponService couponService,
                            CouponRepository couponRepository,
                            CouponRedemptionRepository couponRedemptionRepository, OrderMapper orderMapper) {
        this.cartRepository = cartRepository;
        this.orderRepository = orderRepository;
        this.productVariantRepository = productVariantRepository;
        this.stockMovementRepository = stockMovementRepository;
        this.couponService = couponService;
        this.couponRepository = couponRepository;
        this.couponRedemptionRepository = couponRedemptionRepository;
        this.orderMapper = orderMapper;
    }

    @Override
    @Transactional
    public OrderResponse checkout(Long userId) { return checkout(userId, null); }

    @Override
    @Transactional
    public OrderResponse checkout(Long userId, String couponCode) {
        List<CartLine> cartLines = cartRepository.findByUserId(userId);
        if (cartLines.isEmpty()) {
            throw new BadRequestException("Cannot create an order from an empty cart");
        }

        BigDecimal subtotal = cartLines.stream()
                .map(CartLine::getLineTotal)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        Coupon coupon = couponService.validateAndFind(couponCode, subtotal);
        BigDecimal discount = couponService.calculateDiscount(coupon, subtotal);
        BigDecimal total = subtotal.subtract(discount);
        PurchaseOrder order = new PurchaseOrder();
        order.setUserId(userId);
        order.setSubtotal(subtotal);
        order.setDiscountAmount(discount);
        order.setShippingFee(BigDecimal.ZERO);
        order.setTotal(total);
        order.setStatus("pending");
        orderRepository.insert(order);

        for (CartLine line : cartLines) {
            if (productVariantRepository.decrementStock(line.getVariantId(), line.getQuantity()) == 0) {
                throw new InsufficientStockException("Insufficient stock for variant " + line.getVariantId());
            }
            StockMovement movement = new StockMovement();
            movement.setVariantId(line.getVariantId());
            movement.setChangeAmount(-line.getQuantity());
            movement.setReason("sale");
            movement.setReferenceId(order.getId());
            stockMovementRepository.insert(movement);
            orderRepository.insertLine(order.getId(), line.getVariantId(), line.getProductName(),
                    line.getUnitPrice(), line.getQuantity());
        }
        if (coupon != null) {
            couponRepository.incrementUsedCount(coupon.getId());
            CouponRedemption redemption = new CouponRedemption();
            redemption.setCouponId(coupon.getId());
            redemption.setUserId(userId);
            redemption.setOrderId(order.getId());
            redemption.setDiscountAmount(discount);
            couponRedemptionRepository.insert(redemption);
        }
        cartRepository.clear(userId);
        return findById(userId, order.getId());
    }

    @Override
    public CheckoutPreviewResponse checkoutPreview(Long userId) { return checkoutPreview(userId, null); }

    @Override
    public CheckoutPreviewResponse checkoutPreview(Long userId, String couponCode) {
        BigDecimal subtotal = cartRepository.findByUserId(userId).stream()
                .map(CartLine::getLineTotal).reduce(BigDecimal.ZERO, BigDecimal::add);
        Coupon coupon = couponService.validateAndFind(couponCode, subtotal);
        BigDecimal discount = couponService.calculateDiscount(coupon, subtotal);
        return new CheckoutPreviewResponse(subtotal, discount, BigDecimal.ZERO, subtotal.subtract(discount));
    }

    @Override
    public PageResponse<OrderResponse> findMyOrders(Long userId, String status, int page, int size) {
        if (page < 0 || size < 1 || size > 100) {
            throw new BadRequestException("Invalid pagination parameters");
        }
        long total = orderRepository.countByUserAndStatus(userId, status);
        List<OrderResponse> content = orderRepository.findByUserAndStatus(userId, status, size, page * size).stream()
                .map(this::toResponse).toList();
        int pages = total == 0 ? 0 : (int) Math.ceil((double) total / size);
        return new PageResponse<>(content, total, pages, page, size);
    }

    @Override
    @Transactional
    public OrderResponse cancel(Long userId, Long orderId) {
        PurchaseOrder order = requireOwnedOrder(userId, orderId);
        if (!"pending".equals(order.getStatus())) {
            throw new BadRequestException("Only pending orders can be cancelled");
        }
        for (OrderLine line : orderRepository.findLines(orderId)) {
            productVariantRepository.incrementStock(line.getVariantId(), line.getQuantity());
            StockMovement movement = new StockMovement();
            movement.setVariantId(line.getVariantId());
            movement.setChangeAmount(line.getQuantity());
            movement.setReason("release");
            movement.setReferenceId(orderId);
            stockMovementRepository.insert(movement);
        }
        orderRepository.updateStatus(orderId, "cancelled");
        return findById(userId, orderId);
    }

    @Override
    @Transactional
    public OrderResponse updateStatus(Long orderId, String status) {
        if (!List.of("paid", "shipped", "delivered", "cancelled").contains(status)) {
            throw new BadRequestException("Invalid order status");
        }
        if (orderRepository.updateStatus(orderId, status) == 0) {
            throw new ResourceNotFoundException("Order " + orderId + " was not found");
        }
        PurchaseOrder order = orderRepository.findById(orderId);
        return toResponse(order);
    }

    private PurchaseOrder requireOwnedOrder(Long userId, Long orderId) {
        PurchaseOrder order = orderRepository.findByIdAndUserId(orderId, userId);
        if (order == null) {
            throw new ResourceNotFoundException("Order " + orderId + " was not found");
        }
        return order;
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