package com.example.ecommerce.repository;

import com.example.ecommerce.entity.CouponRedemption;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface CouponRedemptionRepository {
    int insert(CouponRedemption redemption);
}
