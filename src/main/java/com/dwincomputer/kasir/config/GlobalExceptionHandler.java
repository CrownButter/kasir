package com.dwincomputer.kasir.config;

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
public class GlobalExceptionHandler {

    // 1. Menangani Error Validasi (@Valid)
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, Object>> handleValidationErrors(MethodArgumentNotValidException ex) {
        Map<String, String> fieldErrors = new HashMap<>();
        for (FieldError error : ex.getBindingResult().getFieldErrors()) {
            // PERBAIKAN: Gunakan .put() bukan .add()
            fieldErrors.put(error.getField(), error.getDefaultMessage());
        }

        Map<String, Object> result = new HashMap<>();
        result.put("message", "Data tidak valid, periksa kembali inputan Anda.");
        result.put("errors", fieldErrors);
        return new ResponseEntity<>(result, HttpStatus.BAD_REQUEST);
    }

    // 2. Menangani ResponseStatusException (Yang kita pakai di Service tadi)
    @ExceptionHandler(ResponseStatusException.class)
    public ResponseEntity<Map<String, String>> handleResponseStatus(ResponseStatusException ex) {
        Map<String, String> result = new HashMap<>();
        // Mengambil alasan (reason) yang kita tulis di Service
        result.put("message", ex.getReason());
        return new ResponseEntity<>(result, ex.getStatusCode());
    }

    // 3. Menangani Data Duplikat dari Database
    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<Map<String, String>> handleConflict(DataIntegrityViolationException ex) {
        Map<String, String> result = new HashMap<>();
        String msg = ex.getMostSpecificCause().getMessage();

        if (msg.contains("Duplicate entry")) {
            result.put("message", "Gagal menyimpan: Data tersebut (Kode/Nama) sudah ada di sistem.");
        } else {
            result.put("message", "Gagal menyimpan karena batasan data di database.");
        }

        return new ResponseEntity<>(result, HttpStatus.CONFLICT);
    }

    // 4. Menangani Error Akses Ditolak
    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<Map<String, String>> handleAccessDenied(AccessDeniedException ex) {
        Map<String, String> result = new HashMap<>();
        result.put("message", "Akses Ditolak: Anda tidak memiliki izin Admin.");
        return new ResponseEntity<>(result, HttpStatus.FORBIDDEN);
    }

    // 5. Menangani Error Umum lainnya
    @ExceptionHandler(Exception.class)
    public ResponseEntity<Map<String, String>> handleGeneralError(Exception ex) {
        Map<String, String> result = new HashMap<>();
        result.put("message", "Terjadi kesalahan sistem: " + ex.getMessage());
        return new ResponseEntity<>(result, HttpStatus.INTERNAL_SERVER_ERROR);
    }
    @ExceptionHandler({BadCredentialsException.class, InternalAuthenticationServiceException.class})
    public ResponseEntity<Map<String, String>> handleLoginError(Exception ex) {
        Map<String, String> result = new HashMap<>();
        // Kita kirim pesan yang jelas untuk UI
        result.put("message", "Username atau Password yang Anda masukkan salah.");
        return new ResponseEntity<>(result, HttpStatus.UNAUTHORIZED); // 401 Unauthorized
    }
}