package com.example.ecommerce.service.impl;

import com.example.ecommerce.dto.request.ProductRequest;
import com.example.ecommerce.dto.response.ProductResponse;
import com.example.ecommerce.entity.Product;
import com.example.ecommerce.exception.ResourceNotFoundException;
import com.example.ecommerce.mapper.ProductMapper;
import com.example.ecommerce.repository.ProductRepository;
import com.example.ecommerce.service.ProductService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional(readOnly = true)
public class ProductServiceImpl implements ProductService {
    private final ProductRepository productRepository;
    private final ProductMapper productMapper;

    public ProductServiceImpl(ProductRepository productRepository, ProductMapper productMapper) {
        this.productRepository = productRepository;
        this.productMapper = productMapper;
    }

    @Override
    public List<ProductResponse> findAll() {
        return productRepository.findAll().stream().map(productMapper::toResponse).toList();
    }

    @Override
    public ProductResponse findById(Long id) {
        return productMapper.toResponse(requireProduct(id));
    }

    @Override
    @Transactional
    public ProductResponse create(ProductRequest request) {
        Product product = productMapper.toEntity(request);
        productRepository.insert(product);
        return productMapper.toResponse(product);
    }

    @Override
    @Transactional
    public ProductResponse update(Long id, ProductRequest request) {
        Product product = productMapper.toEntity(request);
        product.setId(id);
        if (productRepository.update(product) == 0) {
            throw new ResourceNotFoundException("Product " + id + " was not found");
        }
        return productMapper.toResponse(product);
    }

    @Override
    @Transactional
    public void delete(Long id) {
        if (productRepository.deleteById(id) == 0) {
            throw new ResourceNotFoundException("Product " + id + " was not found");
        }
    }

    private Product requireProduct(Long id) {
        Product product = productRepository.findById(id);
        if (product == null) {
            throw new ResourceNotFoundException("Product " + id + " was not found");
        }
        return product;
    }

}