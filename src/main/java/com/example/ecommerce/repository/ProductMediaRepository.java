package com.example.ecommerce.repository;

import com.example.ecommerce.entity.ProductMedia;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface ProductMediaRepository {
    List<ProductMedia> findByProductId(Long productId);
    List<ProductMedia> findByVariantId(Long variantId);
    int insert(ProductMedia media);
    int deleteByProductId(Long productId);
    int deleteByVariantId(Long variantId);
}
