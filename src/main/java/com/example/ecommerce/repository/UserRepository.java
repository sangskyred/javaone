package com.example.ecommerce.repository;

import com.example.ecommerce.entity.UserAccount;
import org.apache.ibatis.annotations.Mapper;
import java.util.List;

@Mapper
public interface UserRepository {
    UserAccount findByEmail(String email);

    UserAccount findById(Long id);

    int insert(UserAccount account);
    List<String> findRoles(Long userId);
}