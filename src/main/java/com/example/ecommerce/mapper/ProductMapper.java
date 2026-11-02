package com.example.ecommerce.mapper;

import com.example.ecommerce.dto.request.ProductRequest;
import com.example.ecommerce.dto.response.ProductResponse;
import com.example.ecommerce.entity.Product;
import com.example.ecommerce.dto.response.ProductMediaResponse;
import java.util.List;
import org.springframework.stereotype.Component;

@Component
public class ProductMapper {
    public Product toEntity(ProductRequest request) {
        Product product = new Product();
        product.setCategoryId(request.categoryId());
        product.setBrandId(request.brandId());
        product.setName(request.name());
        product.setDescription(request.description());
        return product;
    }

    public ProductResponse toResponse(Product product, com.example.ecommerce.entity.ProductVariant variant) {
        List<ProductMediaResponse> media = (product.getMediaList() == null ? List.<com.example.ecommerce.entity.ProductMedia>of() : product.getMediaList()).stream()
                .map(item -> new ProductMediaResponse(item.getId(), item.getVariantId(), item.getMediaType(), item.getUrl(),
                        item.getThumbnailUrl(), item.getSortOrder(), Boolean.TRUE.equals(item.getMain())))
                .toList();
        return new ProductResponse(product.getId(), product.getName(), product.getDescription(),
                variant == null ? null : variant.getPrice(), variant == null ? null : variant.getStock(), media);
    }
}