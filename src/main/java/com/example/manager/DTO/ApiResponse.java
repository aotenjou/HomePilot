package com.example.manager.DTO;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class ApiResponse<T> {
    private boolean success;
    private T data;
    private String error;
    private String message;
    private LocalDateTime timestamp;

    public ApiResponse() {
        this.timestamp = LocalDateTime.now();
    }

    public ApiResponse(boolean success, T data, String error, String message) {
        this.success = success;
        this.data = data;
        this.error = error;
        this.message = message;
        this.timestamp = LocalDateTime.now();
    }

    public static <T> ApiResponse<T> success(T data) {
        return new ApiResponse<>(true, data, null, "操作成功");
    }

    public static <T> ApiResponse<T> success(T data, String message) {
        return new ApiResponse<>(true, data, null, message);
    }

    public static <T> ApiResponse<T> error(String error) {
        return new ApiResponse<>(false, null, error, "操作失败");
    }

    public static <T> ApiResponse<T> error(String error, String message) {
        return new ApiResponse<>(false, null, error, message);
    }
}

