package com.example.ecommerce.repository;

import com.example.ecommerce.entity.ProductVariant;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface ProductVariantRepository {
    List<ProductVariant> findByProductId(@Param("productId") Long productId);
    ProductVariant findDefaultByProductId(@Param("productId") Long productId);
    ProductVariant findById(Long id);
    int insert(ProductVariant variant);
    int update(ProductVariant variant);
    int deleteById(Long id);
    int decrementStock(@Param("variantId") Long variantId, @Param("quantity") Integer quantity);
    int incrementStock(@Param("variantId") Long variantId, @Param("quantity") Integer quantity);
}
