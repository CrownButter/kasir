package com.dwincomputer.kasir.toko.controller;

import com.dwincomputer.kasir.toko.entity.TokoEntity;
import com.dwincomputer.kasir.toko.service.TokoService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize; // Import ini
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/admin/toko")
@RequiredArgsConstructor
public class TokoController {

    private final TokoService tokoService;

    // Boleh diakses publik (untuk menampilkan Logo & Nama di halaman Login)
    @GetMapping
    public ResponseEntity<?> get() {
        return ResponseEntity.ok(tokoService.get());
    }

    // HANYA ADMIN YANG BOLEH UBAH DATA TOKO
    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping
    public ResponseEntity<?> save(@RequestBody TokoEntity req) {
        return ResponseEntity.ok(tokoService.save(req));
    }
}