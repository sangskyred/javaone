package com.example.ecommerce.repository;

import com.example.ecommerce.entity.CartLine;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface CartRepository {
    List<CartLine> findByUserId(Long userId);

    Integer findQuantity(@Param("userId") Long userId, @Param("productId") Long productId);

    int insert(@Param("userId") Long userId, @Param("productId") Long productId, @Param("quantity") Integer quantity);

    int updateQuantity(@Param("userId") Long userId, @Param("productId") Long productId, @Param("quantity") Integer quantity);

    int deleteItem(@Param("userId") Long userId, @Param("productId") Long productId);

    int clear(Long userId);
}