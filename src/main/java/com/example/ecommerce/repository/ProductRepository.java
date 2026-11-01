package com.example.ecommerce.repository;

import com.example.ecommerce.entity.Product;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface ProductRepository {
    List<Product> findAll();

    Product findById(Long id);

    int insert(Product product);

    int update(Product product);

    int deleteById(Long id);

    int decrementStock(@Param("productId") Long productId, @Param("quantity") Integer quantity);
}