package com.example.ecommerce.service;

import com.example.ecommerce.dto.request.ProductRequest;
import com.example.ecommerce.dto.request.VariantRequest;
import com.example.ecommerce.dto.response.ProductDetailResponse;
import com.example.ecommerce.dto.response.PageResponse;
import com.example.ecommerce.dto.response.ProductResponse;

import java.util.List;

public interface ProductService {
    List<ProductResponse> findAll();

    PageResponse<ProductResponse> search(String keyword, Long categoryId, Long brandId, int page, int size);

    ProductResponse findById(Long id);
    ProductDetailResponse findDetail(Long id);

    ProductResponse create(ProductRequest request);
    ProductResponse createVariant(Long productId, VariantRequest request);
    ProductResponse updateVariant(Long variantId, VariantRequest request);
    void deleteVariant(Long variantId);

    ProductResponse update(Long id, ProductRequest request);

    void delete(Long id);
}