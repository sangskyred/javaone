package com.example.ecommerce.controller;

import com.example.ecommerce.dto.ApiResponse;
import com.example.ecommerce.dto.response.PageResponse;
import com.example.ecommerce.entity.StockMovement;
import com.example.ecommerce.repository.StockMovementRepository;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/admin/stock-movements")
@PreAuthorize("hasAnyRole('ADMIN','STAFF')")
public class AdminStockMovementController {
    private final StockMovementRepository stockMovementRepository;

    public AdminStockMovementController(StockMovementRepository stockMovementRepository) {
        this.stockMovementRepository = stockMovementRepository;
    }

    @GetMapping
    public ApiResponse<PageResponse<StockMovement>> search(@RequestParam(required = false) Long variantId,
                                                           @RequestParam(required = false) String reason,
                                                           @RequestParam(defaultValue = "0") int page,
                                                           @RequestParam(defaultValue = "20") int size) {
        if (page < 0 || size < 1 || size > 100) {
            throw new IllegalArgumentException("Invalid pagination parameters");
        }
        long total = stockMovementRepository.countSearch(variantId, reason);
        int pages = total == 0 ? 0 : (int) Math.ceil((double) total / size);
        return ApiResponse.success(new PageResponse<>(
                stockMovementRepository.search(variantId, reason, size, page * size), total, pages, page, size));
    }
}
