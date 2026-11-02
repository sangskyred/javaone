package com.example.ecommerce.repository;

import com.example.ecommerce.entity.Payment;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface PaymentRepository {
    int insert(Payment payment);
    Payment findByOrderId(Long orderId);
    Payment findByGatewayTransactionId(String gatewayTransactionId);
    int updateStatus(@Param("id") Long id, @Param("status") String status,
                     @Param("gatewayTransactionId") String gatewayTransactionId,
                     @Param("paid") boolean paid);
}
