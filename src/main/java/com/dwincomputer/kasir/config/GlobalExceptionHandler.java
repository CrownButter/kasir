package com.dwincomputer.kasir.config;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException; // Hati-hati salah import
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {

    // Menangani Error Validasi (@Valid)
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, List<String>>> handleValidationErrors(MethodArgumentNotValidException ex) {
        List<String> errors = new ArrayList<>();
        for (FieldError error : ex.getBindingResult().getFieldErrors()) {
            errors.add(error.getDefaultMessage());
        }
        Map<String, List<String>> result = new HashMap<>();
        result.put("errors", errors);
        return new ResponseEntity<>(result, HttpStatus.BAD_REQUEST); // 400
    }

    // Menangani Error Akses Ditolak (@PreAuthorize)
    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<Map<String, String>> handleAccessDenied(AccessDeniedException ex) {
        Map<String, String> result = new HashMap<>();
        result.put("error", "Akses Ditolak: Anda tidak memiliki izin Admin.");
        return new ResponseEntity<>(result, HttpStatus.FORBIDDEN); // 403
    }

    // Menangani Error Umum lainnya
    @ExceptionHandler(Exception.class)
    public ResponseEntity<Map<String, String>> handleGeneralError(Exception ex) {
        Map<String, String> result = new HashMap<>();
        result.put("error", ex.getMessage());
        return new ResponseEntity<>(result, HttpStatus.INTERNAL_SERVER_ERROR); // 500
    }
}