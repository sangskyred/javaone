package com.example.ecommerce.mapper;

import com.example.ecommerce.dto.request.RegisterRequest;
import com.example.ecommerce.dto.response.AccountResponse;
import com.example.ecommerce.entity.UserAccount;
import org.springframework.stereotype.Component;

@Component
public class AccountMapper {
    public UserAccount toEntity(RegisterRequest request, String encodedPassword) {
        UserAccount account = new UserAccount();
        account.setEmail(request.email().trim().toLowerCase());
        account.setPasswordHash(encodedPassword);
        account.setFullName(request.fullName().trim());
        return account;
    }

    public AccountResponse toResponse(UserAccount account) {
        return new AccountResponse(account.getId(), account.getEmail(), account.getFullName());
    }
}