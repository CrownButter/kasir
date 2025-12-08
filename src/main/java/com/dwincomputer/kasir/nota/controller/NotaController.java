package com.dwincomputer.kasir.nota.controller;

import com.dwincomputer.kasir.nota.dto.CreateNotaRequest;
import com.dwincomputer.kasir.nota.entity.NotaEntity;
import com.dwincomputer.kasir.nota.service.NotaService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/nota")
@RequiredArgsConstructor
public class NotaController {

    private final NotaService notaService;

    // === TAMBAHAN BARU ===
    @GetMapping
    public ResponseEntity<?> list() {
        return ResponseEntity.ok(notaService.getAll());
    }
    // =====================

    @PostMapping
    public ResponseEntity<?> create(@RequestBody CreateNotaRequest req) {
        System.out.println(">>> MASUK CONTROLLER! Siap simpan nota untuk: " + req.getCustomerNama());
        return ResponseEntity.ok(notaService.create(req));
    }
    // ==Update Status Pengerjaannya
    @PutMapping("/{id}/status")
    public ResponseEntity<?> updateStatus(@PathVariable Long id, @RequestBody java.util.Map<String, String> body) {
        String newStatus = body.get("status");
        notaService.updateStatus(id, newStatus);
        return ResponseEntity.ok("Status berhasil diupdate");
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> get(@PathVariable Long id) {
        return ResponseEntity.ok(notaService.get(id));
    }

}