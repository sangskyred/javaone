package com.example.ecommerce.service;

import com.example.ecommerce.dto.request.LoginRequest;
import com.example.ecommerce.dto.request.RegisterRequest;
import com.example.ecommerce.dto.response.AccountResponse;

public interface AuthService {
    AccountResponse register(RegisterRequest request);

    AccountResponse login(LoginRequest request);

    AccountResponse findAccount(Long userId);
}