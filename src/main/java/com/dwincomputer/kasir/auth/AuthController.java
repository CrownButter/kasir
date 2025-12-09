package com.dwincomputer.kasir.auth;

import com.dwincomputer.kasir.auth.dto.*;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService service;

    @PostMapping("/register")
    public ResponseEntity<?> register(@Valid @RequestBody RegisterRequest req) {
        return ResponseEntity.ok(service.register(req));
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@Valid @RequestBody LoginRequest req) {
        return ResponseEntity.ok(service.login(req));
    }

    @PostMapping("/refresh-token")
    public ResponseEntity<?> refreshToken(@Valid @RequestBody TokenRefreshRequest req) {
        return ResponseEntity.ok(service.refreshToken(req));
    }

    @PostMapping("/logout")
    public ResponseEntity<?> logout() {
        // Panggil service untuk menangani logika logout
        service.logout();
        return ResponseEntity.ok("Logout berhasil");
    }
}