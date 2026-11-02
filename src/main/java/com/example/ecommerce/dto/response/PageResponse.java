package com.example.ecommerce.dto.response;

import java.util.List;

public record PageResponse<T>(
        List<T> content,
        long totalElements,
        int totalPages,
        int pageNumber,
        int pageSize
) {
}
