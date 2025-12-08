package com.dwincomputer.kasir.nota.controller;

import com.dwincomputer.kasir.nota.dto.CreateNotaRequest;
import com.dwincomputer.kasir.nota.entity.NotaEntity;
import com.dwincomputer.kasir.nota.service.NotaService;
import com.dwincomputer.kasir.print.ThermalPrintService; // Import Printer Service
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/nota") // Pastikan path konsisten (tambah /api jika perlu)
@RequiredArgsConstructor
public class NotaController {

    private final NotaService notaService;
    private final ThermalPrintService printService; // Inject Printer

    @PostMapping
    public ResponseEntity<?> create(@RequestBody CreateNotaRequest req) {
        // 1. Simpan Transaksi
        NotaEntity nota = notaService.create(req);

        // 2. Trigger Print Otomatis
        try {
            printService.printNota(nota);
        } catch (Exception e) {
            // Kita log error saja, jangan gagalkan transaksi jika printer mati/kertas habis
            System.err.println("Gagal mencetak struk: " + e.getMessage());
            e.printStackTrace();
        }

        return ResponseEntity.ok(nota);
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> get(@PathVariable Long id) {
        return ResponseEntity.ok(notaService.get(id));
    }

    // Optional: Endpoint manual untuk print ulang jika kertas macet
    @PostMapping("/{id}/print")
    public ResponseEntity<?> reprint(@PathVariable Long id) {
        NotaEntity nota = notaService.get(id);
        try {
            printService.printNota(nota);
            return ResponseEntity.ok("Perintah cetak dikirim");
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body("Gagal print: " + e.getMessage());
        }
    }
}