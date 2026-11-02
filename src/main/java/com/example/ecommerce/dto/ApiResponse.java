package com.example.ecommerce.dto;

import java.time.OffsetDateTime;

public record ApiResponse<T>(int code, String message, T data, OffsetDateTime timestamp) {
    public static <T> ApiResponse<T> success(T data) {
        return new ApiResponse<>(200, "Success", data, OffsetDateTime.now());
    }

    public static <T> ApiResponse<T> success(int code, String message, T data) {
        return new ApiResponse<>(code, message, data, OffsetDateTime.now());
    }

    public static <T> ApiResponse<T> error(int code, String message) {
        return new ApiResponse<>(code, message, null, OffsetDateTime.now());
    }
}
