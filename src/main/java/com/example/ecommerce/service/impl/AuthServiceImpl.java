package com.example.ecommerce.service.impl;

import com.example.ecommerce.dto.request.LoginRequest;
import com.example.ecommerce.dto.request.RegisterRequest;
import com.example.ecommerce.dto.response.AccountResponse;
import com.example.ecommerce.entity.UserAccount;
import com.example.ecommerce.exception.ConflictException;
import com.example.ecommerce.exception.ResourceNotFoundException;
import com.example.ecommerce.mapper.AccountMapper;
import com.example.ecommerce.repository.UserRepository;
import com.example.ecommerce.security.EcommerceUserDetails;
import com.example.ecommerce.service.AuthService;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class AuthServiceImpl implements AuthService {
    private final UserRepository userRepository;
    private final AccountMapper accountMapper;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;

    public AuthServiceImpl(UserRepository userRepository, AccountMapper accountMapper, PasswordEncoder passwordEncoder,
                           AuthenticationManager authenticationManager) {
        this.userRepository = userRepository;
        this.accountMapper = accountMapper;
        this.passwordEncoder = passwordEncoder;
        this.authenticationManager = authenticationManager;
    }

    @Override
    @Transactional
    public AccountResponse register(RegisterRequest request) {
        String email = request.email().trim().toLowerCase();
        if (userRepository.findByEmail(email) != null) {
            throw new ConflictException("An account with this email already exists");
        }

        UserAccount account = accountMapper.toEntity(request, passwordEncoder.encode(request.password()));
        userRepository.insert(account);
        return accountMapper.toResponse(account);
    }

    @Override
    public AccountResponse login(LoginRequest request) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.email().trim().toLowerCase(), request.password())
        );
        EcommerceUserDetails userDetails = (EcommerceUserDetails) authentication.getPrincipal();
        return accountMapper.toResponse(userDetails.getAccount());
    }

    @Override
    public AccountResponse findAccount(Long userId) {
        UserAccount account = userRepository.findById(userId);
        if (account == null) {
            throw new ResourceNotFoundException("Account " + userId + " was not found");
        }
        return accountMapper.toResponse(account);
    }
}