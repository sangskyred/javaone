package com.example.ecommerce.service.impl;

import com.example.ecommerce.dto.request.CouponRequest;
import com.example.ecommerce.entity.Coupon;
import com.example.ecommerce.exception.BadRequestException;
import com.example.ecommerce.exception.ResourceNotFoundException;
import com.example.ecommerce.repository.CouponRepository;
import com.example.ecommerce.service.CouponService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.OffsetDateTime;
import java.util.List;

@Service
@Transactional(readOnly = true)
public class CouponServiceImpl implements CouponService {
    private final CouponRepository couponRepository;

    public CouponServiceImpl(CouponRepository couponRepository) {
        this.couponRepository = couponRepository;
    }

    @Override public List<Coupon> findAll() { return couponRepository.findAll(); }

    @Override
    public Coupon findById(Long id) {
        Coupon coupon = couponRepository.findById(id);
        if (coupon == null) throw new ResourceNotFoundException("Coupon " + id + " was not found");
        return coupon;
    }

    @Override @Transactional
    public Coupon create(CouponRequest request) {
        if (couponRepository.findByCode(request.code()) != null) throw new BadRequestException("Coupon code already exists");
        return save(null, request);
    }

    @Override @Transactional
    public Coupon update(Long id, CouponRequest request) {
        findById(id);
        return save(id, request);
    }

    private Coupon save(Long id, CouponRequest request) {
        Coupon coupon = new Coupon();
        coupon.setId(id); coupon.setCode(request.code().trim().toUpperCase()); coupon.setDiscountType(request.discountType());
        coupon.setDiscountValue(request.discountValue()); coupon.setMaxUses(request.maxUses()); coupon.setUsedCount(id == null ? 0 : findById(id).getUsedCount());
        coupon.setMinOrderAmount(request.minOrderAmount()); coupon.setStartsAt(request.startsAt()); coupon.setExpiresAt(request.expiresAt()); coupon.setActive(request.isActive());
        if (id == null) couponRepository.insert(coupon); else couponRepository.update(coupon);
        return coupon;
    }

    @Override @Transactional
    public Coupon setActive(Long id, boolean active) {
        if (couponRepository.setActive(id, active) == 0) throw new ResourceNotFoundException("Coupon " + id + " was not found");
        return findById(id);
    }

    @Override @Transactional
    public void delete(Long id) {
        if (couponRepository.deleteById(id) == 0) throw new ResourceNotFoundException("Coupon " + id + " was not found");
    }

    @Override
    public Coupon validateAndFind(String code, BigDecimal subtotal) {
        if (code == null || code.isBlank()) return null;
        Coupon coupon = couponRepository.findByCode(code.trim());
        if (coupon == null) throw new BadRequestException("Coupon code is invalid");
        OffsetDateTime now = OffsetDateTime.now();
        if (!Boolean.TRUE.equals(coupon.getActive())) throw new BadRequestException("Coupon is inactive");
        if (now.isBefore(coupon.getStartsAt()) || coupon.getExpiresAt() != null && now.isAfter(coupon.getExpiresAt())) throw new BadRequestException("Coupon is not in its valid period");
        if (coupon.getMaxUses() != null && coupon.getUsedCount() >= coupon.getMaxUses()) throw new BadRequestException("Coupon usage limit has been reached");
        if (subtotal.compareTo(coupon.getMinOrderAmount()) < 0) throw new BadRequestException("Order total does not meet coupon minimum amount");
        return coupon;
    }

    @Override
    public BigDecimal calculateDiscount(Coupon coupon, BigDecimal subtotal) {
        if (coupon == null) return BigDecimal.ZERO;
        BigDecimal discount = "percent".equals(coupon.getDiscountType())
                ? subtotal.multiply(coupon.getDiscountValue()).divide(BigDecimal.valueOf(100), 2, RoundingMode.HALF_UP)
                : coupon.getDiscountValue();
        return discount.min(subtotal).setScale(2, RoundingMode.HALF_UP);
    }
}
