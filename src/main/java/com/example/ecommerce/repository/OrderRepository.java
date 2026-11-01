package com.example.ecommerce.repository;

import com.example.ecommerce.entity.OrderLine;
import com.example.ecommerce.entity.PurchaseOrder;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface OrderRepository {
    int insert(PurchaseOrder order);

    int insertLine(@Param("orderId") Long orderId, @Param("productId") Long productId,
                   @Param("productName") String productName, @Param("unitPrice") java.math.BigDecimal unitPrice,
                   @Param("quantity") Integer quantity);

    List<PurchaseOrder> findByUserId(Long userId);

    PurchaseOrder findByIdAndUserId(@Param("orderId") Long orderId, @Param("userId") Long userId);

    List<OrderLine> findLines(Long orderId);
}