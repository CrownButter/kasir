package com.dwincomputer.kasir.nota.controller;

import com.dwincomputer.kasir.nota.dto.CreateNotaRequest;
import com.dwincomputer.kasir.nota.service.NotaService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/nota")
@RequiredArgsConstructor
public class NotaController {
    private final NotaService notaService;

    @GetMapping
    public ResponseEntity<?> list() {
        return ResponseEntity.ok(notaService.getAll());
    }

    // Fix: Tambahkan @Valid
    @PostMapping
    public ResponseEntity<?> create(@Valid @RequestBody CreateNotaRequest req) {
        return ResponseEntity.ok(notaService.create(req));
    }

    @PutMapping("/{id}/status")
    public ResponseEntity<?> updateStatus(@PathVariable Long id, @RequestBody Map<String, String> body) {
        String newStatus = body.get("status");
        notaService.updateStatus(id, newStatus);
        return ResponseEntity.ok(Map.of("message", "Status berhasil diupdate"));
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> get(@PathVariable Long id) {
        return ResponseEntity.ok(notaService.get(id));
    }
}