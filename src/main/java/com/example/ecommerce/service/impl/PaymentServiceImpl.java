package com.example.ecommerce.service.impl;

import com.example.ecommerce.dto.request.PaymentInitiateRequest;
import com.example.ecommerce.dto.request.PaymentWebhookRequest;
import com.example.ecommerce.entity.Payment;
import com.example.ecommerce.entity.PurchaseOrder;
import com.example.ecommerce.exception.BadRequestException;
import com.example.ecommerce.exception.ResourceNotFoundException;
import com.example.ecommerce.repository.OrderRepository;
import com.example.ecommerce.repository.PaymentRepository;
import com.example.ecommerce.service.PaymentService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.util.HexFormat;

@Service
public class PaymentServiceImpl implements PaymentService {
    private final PaymentRepository paymentRepository;
    private final OrderRepository orderRepository;
    private final String webhookSecret;

    public PaymentServiceImpl(PaymentRepository paymentRepository, OrderRepository orderRepository,
                              @Value("${payment.webhook-secret:dev-webhook-secret}") String webhookSecret) {
        this.paymentRepository = paymentRepository;
        this.orderRepository = orderRepository;
        this.webhookSecret = webhookSecret;
    }

    @Override
    @Transactional
    public Payment initiate(Long userId, PaymentInitiateRequest request) {
        PurchaseOrder order = orderRepository.findByIdAndUserId(request.orderId(), userId);
        if (order == null) throw new ResourceNotFoundException("Order " + request.orderId() + " was not found");
        Payment existing = paymentRepository.findByOrderId(order.getId());
        if (existing != null && "succeeded".equals(existing.getStatus())) throw new BadRequestException("Order is already paid");
        Payment payment = new Payment();
        payment.setOrderId(order.getId()); payment.setAmount(order.getTotal()); payment.setCurrency("VND");
        payment.setMethod(request.method()); payment.setStatus("pending");
        payment.setGatewayTransactionId("GW-" + request.method().toUpperCase() + "-" + order.getId() + "-" + System.currentTimeMillis());
        paymentRepository.insert(payment);
        return payment;
    }

    @Override
    @Transactional
    public Payment webhook(String gateway, PaymentWebhookRequest request) {
        if (!isValidSignature(request.transactionId(), gateway, request.signature())) throw new BadRequestException("Invalid payment webhook signature");
        Payment payment = paymentRepository.findByGatewayTransactionId(request.transactionId());
        if (payment == null) throw new ResourceNotFoundException("Payment transaction was not found");
        boolean succeeded = "succeeded".equals(request.status());
        paymentRepository.updateStatus(payment.getId(), succeeded ? "succeeded" : "failed", request.transactionId(), succeeded);
        if (succeeded) orderRepository.updateStatus(payment.getOrderId(), "paid");
        payment.setStatus(succeeded ? "succeeded" : "failed");
        return payment;
    }

    private boolean isValidSignature(String transactionId, String gateway, String signature) {
        if (signature == null || signature.isBlank()) return false;
        try {
            Mac mac = Mac.getInstance("HmacSHA256");
            mac.init(new SecretKeySpec((webhookSecret + ":" + gateway).getBytes(StandardCharsets.UTF_8), "HmacSHA256"));
            String expected = HexFormat.of().formatHex(mac.doFinal(transactionId.getBytes(StandardCharsets.UTF_8)));
            return expected.equalsIgnoreCase(signature);
        } catch (Exception exception) { throw new IllegalStateException("Cannot verify payment signature", exception); }
    }
}
