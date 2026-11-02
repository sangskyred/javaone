package com.example.ecommerce.controller;

import com.example.ecommerce.dto.ApiResponse;
import com.example.ecommerce.dto.request.CouponRequest;
import com.example.ecommerce.entity.Coupon;
import com.example.ecommerce.service.CouponService;
import jakarta.validation.Valid;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/admin/coupons")
@PreAuthorize("hasAnyRole('ADMIN','STAFF')")
public class AdminCouponController {
    private final CouponService couponService;

    public AdminCouponController(CouponService couponService) { this.couponService = couponService; }

    @GetMapping public ApiResponse<List<Coupon>> findAll() { return ApiResponse.success(couponService.findAll()); }
    @GetMapping("/{id}") public ApiResponse<Coupon> findById(@PathVariable Long id) { return ApiResponse.success(couponService.findById(id)); }
    @PostMapping public ApiResponse<Coupon> create(@Valid @RequestBody CouponRequest request) { return ApiResponse.success(couponService.create(request)); }
    @PutMapping("/{id}") public ApiResponse<Coupon> update(@PathVariable Long id, @Valid @RequestBody CouponRequest request) { return ApiResponse.success(couponService.update(id, request)); }
    @PutMapping("/{id}/active") public ApiResponse<Coupon> setActive(@PathVariable Long id, @RequestBody Map<String, Boolean> body) { return ApiResponse.success(couponService.setActive(id, Boolean.TRUE.equals(body.get("active")))); }
    @DeleteMapping("/{id}") public ApiResponse<Void> delete(@PathVariable Long id) { couponService.delete(id); return ApiResponse.success(200, "Coupon deleted", null); }
}
