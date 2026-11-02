package com.example.ecommerce.controller;

import com.example.ecommerce.dto.ApiResponse;
import com.example.ecommerce.dto.request.PaymentInitiateRequest;
import com.example.ecommerce.dto.request.PaymentWebhookRequest;
import com.example.ecommerce.entity.Payment;
import com.example.ecommerce.exception.BadRequestException;
import com.example.ecommerce.security.EcommerceUserDetails;
import com.example.ecommerce.service.PaymentService;
import jakarta.validation.Valid;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.http.ResponseEntity;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/payments")
public class PaymentController {
    private final PaymentService paymentService;

    public PaymentController(PaymentService paymentService) { this.paymentService = paymentService; }

    @PostMapping("/initiate")
    public ApiResponse<Payment> initiate(@AuthenticationPrincipal EcommerceUserDetails user,
                                         @Valid @RequestBody PaymentInitiateRequest request) {
        return ApiResponse.success(paymentService.initiate(user.getAccount().getId(), request));
    }

    @PostMapping("/webhook/{gateway}")
    public ResponseEntity<Map<String, Object>> webhook(@PathVariable String gateway,
                                                        @Valid @RequestBody PaymentWebhookRequest request) {
        String normalizedGateway = gateway.toLowerCase();
        if (!List.of("cod", "bank_transfer", "momo", "vnpay", "paypal", "stripe").contains(normalizedGateway)) {
            throw new BadRequestException("Unsupported payment gateway");
        }
        Payment payment = paymentService.webhook(normalizedGateway, request);
        Map<String, Object> response = new LinkedHashMap<>();
        if ("vnpay".equals(normalizedGateway)) {
            response.put("RspCode", "00");
            response.put("Message", "Confirm success");
        } else if ("momo".equals(normalizedGateway)) {
            response.put("resultCode", 0);
            response.put("message", "Successful");
            response.put("partnerCode", "MOMO");
            response.put("requestId", payment.getGatewayTransactionId());
        } else if ("stripe".equals(normalizedGateway)) {
            response.put("received", true);
        } else {
            response.put("code", "00");
            response.put("message", "Success");
            response.put("accepted", true);
        }
        return ResponseEntity.ok(response);
    }
}
