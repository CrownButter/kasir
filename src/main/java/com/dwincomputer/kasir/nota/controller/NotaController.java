package com.dwincomputer.kasir.nota.controller;

import com.dwincomputer.kasir.nota.dto.CreateNotaRequest;
import com.dwincomputer.kasir.nota.entity.NotaEntity;
import com.dwincomputer.kasir.nota.service.NotaService;
import com.dwincomputer.kasir.nota.service.PdfService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.Map;

@RestController
@RequestMapping("/api/nota")
@RequiredArgsConstructor
public class NotaController {

    private final NotaService notaService;
    private final PdfService pdfService;

    // --- 1. LIST SEMUA NOTA ---
    @GetMapping
    public ResponseEntity<?> list() {
        return ResponseEntity.ok(notaService.getAll());
    }

    // --- 2. DETAIL NOTA ---
    @GetMapping("/{id}")
    public ResponseEntity<?> get(@PathVariable Long id) {
        return ResponseEntity.ok(notaService.get(id));
    }

    // --- 3. BUAT NOTA BARU (Hanya SATU method create di sini) ---
    @PostMapping
    public ResponseEntity<?> create(@Valid @RequestBody CreateNotaRequest req) {
        return ResponseEntity.ok(notaService.create(req));
    }

    // --- 4. EDIT DATA NOTA ---
    @PutMapping("/{id}")
    public ResponseEntity<?> update(@PathVariable Long id, @Valid @RequestBody CreateNotaRequest req) {
        return ResponseEntity.ok(notaService.update(id, req));
    }

    // --- 5. UPDATE STATUS (BATAL/PROSES/SELESAI) ---
    @PutMapping("/{id}/status")
    public ResponseEntity<?> updateStatus(@PathVariable Long id, @RequestBody Map<String, String> body) {
        String newStatus = body.get("status");
        notaService.updateStatus(id, newStatus);
        return ResponseEntity.ok(Map.of("message", "Status berhasil diupdate"));
    }

    // --- 6. DOWNLOAD PDF ---
    @GetMapping("/{id}/pdf")
    public ResponseEntity<byte[]> downloadPdf(@PathVariable Long id) throws IOException {
        // Ambil data nota
        NotaEntity nota = notaService.get(id);

        // Generate PDF byte array dari Service
        byte[] pdfBytes = pdfService.generateNotaPdf(nota);

        // Return sebagai file download
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=Nota-" + nota.getKodeNota() + ".pdf")
                .contentType(MediaType.APPLICATION_PDF)
                .body(pdfBytes);
    }

    @PostMapping
    public Nota simpan(@RequestBody Nota nota) {
        return service.simpanNota(nota); // Mengembalikan objek Nota hasil simpan
    }
}