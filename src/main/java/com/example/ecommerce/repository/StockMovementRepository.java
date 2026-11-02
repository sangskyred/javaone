package com.example.ecommerce.repository;

import com.example.ecommerce.entity.StockMovement;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import java.util.List;

@Mapper
public interface StockMovementRepository {
    int insert(StockMovement movement);
    List<StockMovement> search(@Param("variantId") Long variantId,
                               @Param("reason") String reason,
                               @Param("limit") int limit,
                               @Param("offset") int offset);
    long countSearch(@Param("variantId") Long variantId, @Param("reason") String reason);
}
