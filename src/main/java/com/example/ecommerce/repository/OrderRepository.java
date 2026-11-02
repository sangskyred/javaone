package com.example.ecommerce.repository;

import com.example.ecommerce.entity.OrderLine;
import com.example.ecommerce.entity.PurchaseOrder;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface OrderRepository {
    int insert(PurchaseOrder order);

    int insertLine(@Param("orderId") Long orderId, @Param("variantId") Long variantId,
                   @Param("productName") String productName, @Param("unitPrice") java.math.BigDecimal unitPrice,
                   @Param("quantity") Integer quantity);

    List<PurchaseOrder> findByUserId(Long userId);

    List<PurchaseOrder> findByUserAndStatus(@Param("userId") Long userId,
                                            @Param("status") String status,
                                            @Param("limit") int limit,
                                            @Param("offset") int offset);

    long countByUserAndStatus(@Param("userId") Long userId, @Param("status") String status);

    int updateStatus(@Param("orderId") Long orderId, @Param("status") String status);

    PurchaseOrder findById(Long orderId);

    PurchaseOrder findByIdAndUserId(@Param("orderId") Long orderId, @Param("userId") Long userId);

    List<OrderLine> findLines(Long orderId);
}