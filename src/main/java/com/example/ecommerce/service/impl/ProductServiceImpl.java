package com.example.ecommerce.service.impl;

import com.example.ecommerce.dto.request.ProductRequest;
import com.example.ecommerce.dto.request.VariantRequest;
import com.example.ecommerce.dto.response.ProductDetailResponse;
import com.example.ecommerce.dto.response.ProductMediaResponse;
import com.example.ecommerce.dto.response.VariantResponse;
import com.example.ecommerce.dto.response.PageResponse;
import com.example.ecommerce.dto.response.ProductResponse;
import com.example.ecommerce.entity.Product;
import com.example.ecommerce.entity.ProductMedia;
import com.example.ecommerce.entity.ProductVariant;
import com.example.ecommerce.repository.ProductMediaRepository;
import com.example.ecommerce.exception.ResourceNotFoundException;
import com.example.ecommerce.mapper.ProductMapper;
import com.example.ecommerce.repository.ProductRepository;
import com.example.ecommerce.repository.ProductVariantRepository;
import com.example.ecommerce.service.ProductService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional(readOnly = true)
public class ProductServiceImpl implements ProductService {
    private final ProductRepository productRepository;
    private final ProductVariantRepository productVariantRepository;
    private final ProductMediaRepository productMediaRepository;
    private final ProductMapper productMapper;

    public ProductServiceImpl(ProductRepository productRepository,
                              ProductVariantRepository productVariantRepository,
                              ProductMediaRepository productMediaRepository,
                              ProductMapper productMapper) {
        this.productRepository = productRepository;
        this.productVariantRepository = productVariantRepository;
        this.productMediaRepository = productMediaRepository;
        this.productMapper = productMapper;
    }

    @Override
    public PageResponse<ProductResponse> search(String keyword, Long categoryId, Long brandId, int page, int size) {
        if (page < 0) {
            throw new com.example.ecommerce.exception.BadRequestException("page must be zero or greater");
        }
        if (size < 1 || size > 100) {
            throw new com.example.ecommerce.exception.BadRequestException("size must be between 1 and 100");
        }
        String normalizedKeyword = keyword == null || keyword.isBlank() ? null : keyword.trim();
        long totalElements = productRepository.countSearch(normalizedKeyword, categoryId, brandId);
        int totalPages = totalElements == 0 ? 0 : (int) Math.ceil((double) totalElements / size);
        List<ProductResponse> content = productRepository.search(normalizedKeyword, categoryId, brandId,
                        size, page * size).stream()
                .map(product -> productMapper.toResponse(product,
                        productVariantRepository.findDefaultByProductId(product.getId())))
                .toList();
        return new PageResponse<>(content, totalElements, totalPages, page, size);
    }

    @Override
    public List<ProductResponse> findAll() {
        return productRepository.findAll().stream()
                .map(product -> productMapper.toResponse(product,
                        productVariantRepository.findDefaultByProductId(product.getId())))
                .toList();
    }

    @Override
    public ProductDetailResponse findDetail(Long id) {
        Product product = requireProduct(id);
        List<VariantResponse> variants = productVariantRepository.findByProductId(id).stream()
                .filter(variant -> Boolean.TRUE.equals(variant.getActive()))
                .map(variant -> new VariantResponse(variant.getId(), variant.getSku(), variant.getName(),
                        variant.getAttributes(), variant.getPrice(), variant.getStock(),
                        Boolean.TRUE.equals(variant.getActive()), productMediaRepository.findByVariantId(variant.getId()).stream()
                                .map(this::toMediaResponse).toList()))
                .toList();
        return new ProductDetailResponse(product.getId(), product.getName(), product.getDescription(), variants);
    }

    @Override
    public ProductResponse findById(Long id) {
        Product product = requireProduct(id);
        return productMapper.toResponse(product,
                productVariantRepository.findDefaultByProductId(product.getId()));
    }

    @Override
    @Transactional
    public ProductResponse create(ProductRequest request) {
        Product product = productMapper.toEntity(request);
        productRepository.insert(product);
        saveProductMedia(product.getId(), request.media());
        return productMapper.toResponse(product, null);
    }

    @Override
    @Transactional
    public ProductResponse update(Long id, ProductRequest request) {
        Product product = productMapper.toEntity(request);
        product.setId(id);
        if (productRepository.update(product) == 0) {
            throw new ResourceNotFoundException("Product " + id + " was not found");
        }
        productMediaRepository.deleteByProductId(id);
        saveProductMedia(id, request.media());
        ProductVariant variant = productVariantRepository.findDefaultByProductId(id);
        return productMapper.toResponse(product, variant);
    }

    @Override
    @Transactional
    public ProductResponse createVariant(Long productId, VariantRequest request) {
        requireProduct(productId);
        ProductVariant variant = variantFromRequest(productId, null, request);
        productVariantRepository.insert(variant);
        saveVariantMedia(productId, variant.getId(), request.media());
        return productMapper.toResponse(requireProduct(productId), variant);
    }

    @Override
    @Transactional
    public ProductResponse updateVariant(Long variantId, VariantRequest request) {
        ProductVariant variant = productVariantRepository.findById(variantId);
        if (variant == null) {
            throw new ResourceNotFoundException("Variant " + variantId + " was not found");
        }
        variant.setSku(request.sku());
        variant.setName(request.name());
        variant.setAttributes(request.attributes());
        variant.setPrice(request.price());
        variant.setStock(request.stock());
        variant.setActive(request.isActive());
        productVariantRepository.update(variant);
        productMediaRepository.deleteByVariantId(variantId);
        saveVariantMedia(variant.getProductId(), variantId, request.media());
        return productMapper.toResponse(requireProduct(variant.getProductId()), variant);
    }

    @Override
    @Transactional
    public void deleteVariant(Long variantId) {
        if (productVariantRepository.deleteById(variantId) == 0) {
            throw new ResourceNotFoundException("Variant " + variantId + " was not found");
        }
    }

    @Override
    @Transactional
    public void delete(Long id) {
        if (productRepository.deleteById(id) == 0) {
            throw new ResourceNotFoundException("Product " + id + " was not found");
        }
    }

    private ProductVariant variantFromRequest(Long productId, Long variantId, VariantRequest request) {
        ProductVariant variant = new ProductVariant();
        variant.setId(variantId);
        variant.setProductId(productId);
        variant.setSku(request.sku());
        variant.setName(request.name());
        variant.setAttributes(request.attributes() == null ? java.util.Collections.emptyMap() : request.attributes());
        variant.setPrice(request.price());
        variant.setStock(request.stock());
        variant.setActive(request.isActive());
        return variant;
    }

    private void saveProductMedia(Long productId, List<com.example.ecommerce.dto.request.ProductMediaRequest> mediaList) {
        if (mediaList == null) return;
        for (int index = 0; index < mediaList.size(); index++) {
            var request = mediaList.get(index);
            ProductMedia media = new ProductMedia();
            media.setProductId(productId);
            media.setMediaType(request.mediaType());
            media.setUrl(request.url());
            media.setThumbnailUrl(request.thumbnailUrl());
            media.setSortOrder(request.sortOrder() == null ? index : request.sortOrder());
            media.setMain(Boolean.TRUE.equals(request.isMain()));
            productMediaRepository.insert(media);
        }
    }

    private void saveVariantMedia(Long productId, Long variantId,
                                  List<com.example.ecommerce.dto.request.ProductMediaRequest> mediaList) {
        if (mediaList == null) return;
        for (int index = 0; index < mediaList.size(); index++) {
            var request = mediaList.get(index);
            ProductMedia media = new ProductMedia();
            media.setProductId(productId);
            media.setVariantId(variantId);
            media.setMediaType(request.mediaType());
            media.setUrl(request.url());
            media.setThumbnailUrl(request.thumbnailUrl());
            media.setSortOrder(request.sortOrder() == null ? index : request.sortOrder());
            media.setMain(Boolean.TRUE.equals(request.isMain()));
            productMediaRepository.insert(media);
        }
    }

    private ProductMediaResponse toMediaResponse(ProductMedia media) {
        return new ProductMediaResponse(media.getId(), media.getVariantId(), media.getMediaType(), media.getUrl(),
                media.getThumbnailUrl(), media.getSortOrder(), Boolean.TRUE.equals(media.getMain()));
    }

    private Product requireProduct(Long id) {
        Product product = productRepository.findById(id);
        if (product == null) {
            throw new ResourceNotFoundException("Product " + id + " was not found");
        }
        return product;
    }

}