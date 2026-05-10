package com.payflow.payflow.exception;

import com.payflow.payflow.dto.response.ApiResponse;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(ResourceNotFoundException.class)
    public ApiResponse<Void> handleResourceNotFound(ResourceNotFoundException ex) {

        return new ApiResponse<>(
                false,
                ex.getMessage(),
                null
        );
    }
}