package com.esoft.web.filter;

import com.esoft.service.errors.BadRequestAlertException;
import com.esoft.service.errors.BusinessException;
import com.esoft.service.errors.UnauthorizedException;
import com.esoft.web.rest.dto.ApiResponse;
import com.esoft.web.rest.dto.ResponseStatus;
import jakarta.validation.ConstraintViolationException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.HashMap;
import java.util.Map;

@RestControllerAdvice(basePackages = "com.esoft.web.rest.external")
public class ExternalAPIExceptionHandler {
    private static final Logger LOG = LoggerFactory.getLogger(ExternalAPIExceptionHandler.class);
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiResponse<Void>> handleValidationExceptions(MethodArgumentNotValidException ex) {
        LOG.error("Validation error: {}", ex.getMessage(), ex);
        Map<String, String> errors = new HashMap<>();
        ex.getBindingResult().getFieldErrors().forEach(error ->
            errors.put(error.getField(), error.getDefaultMessage())
        );
        return ResponseEntity.badRequest().body(
            ApiResponse.<Void>builder()
                .status(ResponseStatus.ERROR)
                .message("Validation failed")
                .errors(errors)
                .build()
        );
    }

    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<ApiResponse<Void>> handleConstraintViolation(ConstraintViolationException ex) {
        LOG.error("Constraint violation error: {}", ex.getMessage(), ex);
        Map<String, String> errors = new HashMap<>();
        ex.getConstraintViolations().forEach(violation ->
            errors.put(violation.getPropertyPath().toString(), violation.getMessage())
        );
        return ResponseEntity.badRequest().body(
            ApiResponse.<Void>builder()
                .status(ResponseStatus.ERROR)
                .message("Validation failed")
                .errors(errors)
                .build()
        );
    }

    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<ApiResponse<Void>> handleRuntimeException(RuntimeException ex) {
        LOG.error("Runtime exception: {}", ex.getMessage(), ex);
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(
                ApiResponse.<Void>builder()
                    .status(ResponseStatus.ERROR)
            .message(ex.getMessage())
            .errors(Map.of("error", ex.getMessage()))
            .build()
        );
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiResponse<Void>> handleGenericException(Exception ex) {
        LOG.error("Unexpected error: {}", ex.getMessage(), ex);
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(
            ApiResponse.<Void>builder()
                .status(ResponseStatus.ERROR)
                .message("An unexpected error occurred")
                .errors(Map.of("error", ex.getMessage()))
                .build()
        );
    }

    @ExceptionHandler(UnauthorizedException.class)
    public ResponseEntity<Object> handleUnauthorized(UnauthorizedException ex) {
        LOG.error("Unauthorized access: {}", ex.getMessage(), ex);
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
            .body(Map.of("message", ex.getMessage()));
    }

    @ExceptionHandler(BusinessException.class)
    public ResponseEntity<ApiResponse<Void>> handleApiException(BusinessException ex) {
        LOG.error("Business exception: {}", ex.getMessage(), ex);
        return ResponseEntity.status(ex.getStatus()).body(
            ApiResponse.<Void>builder()
                .status(ResponseStatus.ERROR)
                .message(ex.getMessage())
                .errors(Map.of("error", ex.getMessage()))
                .build()
        );
    }

    @ExceptionHandler(BadRequestAlertException.class)
    public ResponseEntity<ApiResponse<Void>> handleApiException(BadRequestAlertException ex) {
        LOG.error("Bad request: {}", ex.getMessage(), ex);
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(
            ApiResponse.<Void>builder()
                .status(ResponseStatus.ERROR)
                .message(ex.getProblemDetailWithCause().getTitle())
                .errors(Map.of("error", ex.getMessage()))
                .build()
        );
    }
}
