package com.dwincomputer.kasir.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.InternalAuthenticationServiceException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.server.ResponseStatusException;

import java.util.HashMap;
import java.util.Map;

@RestControllerAdvice
@Slf4j // Menambahkan fitur Logging otomatis (Lombok)
public class GlobalExceptionHandler {

    // 1. Menangani Error Validasi (@Valid) - Error 400
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, Object>> handleValidationErrors(MethodArgumentNotValidException ex) {
        Map<String, String> fieldErrors = new HashMap<>();
        for (FieldError error : ex.getBindingResult().getFieldErrors()) {
            fieldErrors.put(error.getField(), error.getDefaultMessage());
        }

        Map<String, Object> result = new HashMap<>();
        result.put("message", "Input tidak valid. Periksa kembali data Anda.");
        result.put("errors", fieldErrors);

        log.warn("Gagal validasi input: {}", fieldErrors); // Log warning saja
        return new ResponseEntity<>(result, HttpStatus.BAD_REQUEST);
    }

    // 2. Menangani ResponseStatusException (Custom Throw)
    @ExceptionHandler(ResponseStatusException.class)
    public ResponseEntity<Map<String, String>> handleResponseStatus(ResponseStatusException ex) {
        Map<String, String> result = new HashMap<>();
        result.put("message", ex.getReason());

        log.warn("Response Status Error: {}", ex.getReason());
        return new ResponseEntity<>(result, ex.getStatusCode());
    }

    // 3. Menangani Data Duplikat / Error Database - Error 409
    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<Map<String, String>> handleConflict(DataIntegrityViolationException ex) {
        Map<String, String> result = new HashMap<>();
        String msg = ex.getMostSpecificCause().getMessage();

        // Null check agar aman
        if (msg != null && msg.contains("Duplicate entry")) {
            result.put("message", "Gagal menyimpan: Data (Kode/Username/Email) sudah terdaftar.");
        } else {
            result.put("message", "Gagal menyimpan karena konflik data di database.");
        }

        log.error("Database Conflict: {}", msg);
        return new ResponseEntity<>(result, HttpStatus.CONFLICT);
    }

    // 4. Menangani Error Akses Ditolak - Error 403
    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<Map<String, String>> handleAccessDenied(AccessDeniedException ex) {
        Map<String, String> result = new HashMap<>();
        // Pesan netral, tidak menyebut "Admin"
        result.put("message", "Akses Ditolak: Anda tidak memiliki izin untuk melakukan aksi ini.");

        log.warn("Percobaan akses ditolak: {}", ex.getMessage());
        return new ResponseEntity<>(result, HttpStatus.FORBIDDEN);
    }

    // 5. Menangani Gagal Login - Error 401
    @ExceptionHandler({BadCredentialsException.class, InternalAuthenticationServiceException.class})
    public ResponseEntity<Map<String, String>> handleLoginError(Exception ex) {
        Map<String, String> result = new HashMap<>();
        result.put("message", "Username atau Password salah.");

        log.warn("Gagal login: {}", ex.getMessage());
        return new ResponseEntity<>(result, HttpStatus.UNAUTHORIZED);
    }

    // 6. Menangani Error Umum Lainnya (Fallback) - Error 500
    @ExceptionHandler(Exception.class)
    public ResponseEntity<Map<String, String>> handleGeneralError(Exception ex) {
        Map<String, String> result = new HashMap<>();

        // PENTING: Jangan tampilkan ex.getMessage() mentah ke user di Production!
        // User cukup tahu ada error, tapi detailnya kita simpan di log server.
        result.put("message", "Terjadi kesalahan internal pada server.");

        // Log error lengkap (Stack Trace) agar Anda bisa debug di Terminal
        log.error("SYSTEM ERROR (500): ", ex);

        return new ResponseEntity<>(result, HttpStatus.INTERNAL_SERVER_ERROR);
    }
}