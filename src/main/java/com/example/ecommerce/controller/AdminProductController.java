package com.example.ecommerce.controller;

import com.example.ecommerce.dto.ApiResponse;
import com.example.ecommerce.dto.request.ProductRequest;
import com.example.ecommerce.dto.request.VariantRequest;
import com.example.ecommerce.dto.response.ProductResponse;
import com.example.ecommerce.service.ProductService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/admin/products")
@PreAuthorize("hasAnyRole('ADMIN','STAFF')")
public class AdminProductController {
    private final ProductService productService;

    public AdminProductController(ProductService productService) {
        this.productService = productService;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ApiResponse<ProductResponse> create(@Valid @RequestBody ProductRequest request) {
        return ApiResponse.success(HttpStatus.CREATED.value(), "Product created", productService.create(request));
    }

    @PutMapping("/{id}")
    public ApiResponse<ProductResponse> update(@PathVariable Long id, @Valid @RequestBody ProductRequest request) {
        return ApiResponse.success(productService.update(id, request));
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public ApiResponse<Void> delete(@PathVariable Long id) {
        productService.delete(id);
        return ApiResponse.success(HttpStatus.NO_CONTENT.value(), "Product deleted", null);
    }

    @PostMapping("/{id}/variants")
    public ApiResponse<ProductResponse> createVariant(@PathVariable Long id,
                                                       @Valid @RequestBody VariantRequest request) {
        return ApiResponse.success(productService.createVariant(id, request));
    }

    @PutMapping("/variants/{variantId}")
    public ApiResponse<ProductResponse> updateVariant(@PathVariable Long variantId,
                                                       @Valid @RequestBody VariantRequest request) {
        return ApiResponse.success(productService.updateVariant(variantId, request));
    }

    @DeleteMapping("/variants/{variantId}")
    public ApiResponse<Void> deleteVariant(@PathVariable Long variantId) {
        productService.deleteVariant(variantId);
        return ApiResponse.success(200, "Variant deleted", null);
    }
}
