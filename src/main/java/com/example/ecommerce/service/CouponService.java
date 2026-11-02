package com.example.ecommerce.service;

import com.example.ecommerce.dto.request.CouponRequest;
import com.example.ecommerce.entity.Coupon;
import java.math.BigDecimal;
import java.util.List;

public interface CouponService {
    List<Coupon> findAll();
    Coupon findById(Long id);
    Coupon create(CouponRequest request);
    Coupon update(Long id, CouponRequest request);
    Coupon setActive(Long id, boolean active);
    void delete(Long id);
    Coupon validateAndFind(String code, BigDecimal subtotal);
    BigDecimal calculateDiscount(Coupon coupon, BigDecimal subtotal);
}
