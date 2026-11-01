package com.example.ecommerce.security;

import com.example.ecommerce.entity.UserAccount;
import com.example.ecommerce.repository.UserRepository;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class EcommerceUserDetailsService implements UserDetailsService {
    private final UserRepository userRepository;

    public EcommerceUserDetailsService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String email) {
        UserAccount account = userRepository.findByEmail(email.toLowerCase());
        if (account == null) {
            throw new UsernameNotFoundException("Account was not found");
        }
        return new EcommerceUserDetails(account);
    }
}