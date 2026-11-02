package com.example.ecommerce.service;

import com.example.ecommerce.dto.request.PaymentInitiateRequest;
import com.example.ecommerce.dto.request.PaymentWebhookRequest;
import com.example.ecommerce.entity.Payment;

public interface PaymentService {
    Payment initiate(Long userId, PaymentInitiateRequest request);
    Payment webhook(String gateway, PaymentWebhookRequest request);
}
