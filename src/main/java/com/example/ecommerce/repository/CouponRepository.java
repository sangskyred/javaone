package com.example.ecommerce.repository;

import com.example.ecommerce.entity.Coupon;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface CouponRepository {
    Coupon findById(Long id);
    Coupon findByCode(String code);
    List<Coupon> findAll();
    int insert(Coupon coupon);
    int update(Coupon coupon);
    int setActive(@Param("id") Long id, @Param("active") Boolean active);
    int deleteById(Long id);
    int incrementUsedCount(Long id);
}
