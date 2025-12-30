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
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.Map;

@RestController
@RequestMapping("/api/nota")
@RequiredArgsConstructor
public class NotaController {

    private final NotaService notaService;
    private final PdfService pdfService;

    // --- 1. LAPORAN OMZET ---
    // SENSITIF: Hanya Manager dan Admin yang boleh lihat rekap uang
    @GetMapping("/report")
    @PreAuthorize("hasRole('ADMIN') or hasRole('MANAGER') or hasAuthority('VIEW_REPORT')")
    public ResponseEntity<?> report(
            @RequestParam("start") String start,
            @RequestParam("end") String end) {
        return ResponseEntity.ok(notaService.getReport(start, end));
    }

    // --- 2. LIST SEMUA NOTA ---
    // Public (Authenticated): Kasir perlu lihat history transaksi hari ini
    @GetMapping
    public ResponseEntity<?> list() {
        return ResponseEntity.ok(notaService.getAll());
    }

    // --- 3. DETAIL NOTA ---
    // Public (Authenticated): Kasir perlu cetak ulang nota
    @GetMapping("/{id}")
    public ResponseEntity<?> get(@PathVariable Long id) {
        return ResponseEntity.ok(notaService.get(id));
    }

    // --- 4. BUAT NOTA BARU ---
    // Public (Authenticated): Fungsi utama Kasir
    @PostMapping
    public ResponseEntity<?> create(@Valid @RequestBody CreateNotaRequest req) {
        NotaEntity savedNota = notaService.create(req);
        return ResponseEntity.ok(savedNota);
    }

    // --- 5. EDIT DATA NOTA (Full Edit) ---
    // Logic: Jika ada kesalahan input item/harga setelah disimpan,
    // hanya Admin/Manager atau Kasir Senior (TRANSACTION_EDIT) yang boleh ubah.
    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN') or hasRole('MANAGER') or hasAuthority('TRANSACTION_EDIT')")
    public ResponseEntity<?> update(@PathVariable Long id, @Valid @RequestBody CreateNotaRequest req) {
        return ResponseEntity.ok(notaService.update(id, req));
    }

    // --- 6. UPDATE STATUS (BATAL/PROSES/SELESAI) ---
    // Logic Hybrid:
    // - Update ke 'LUNAS'/'SELESAI': Boleh semua Kasir.
    // - Update ke 'BATAL': HANYA Manager/Admin atau Permission 'TRANSACTION_VOID'.
    @PutMapping("/{id}/status")
    public ResponseEntity<?> updateStatus(@PathVariable Long id, @RequestBody Map<String, String> body) {
        String newStatus = body.get("status");

        // CEK MANUAL KHUSUS PEMBATALAN
        if ("BATAL".equalsIgnoreCase(newStatus)) {
            Authentication auth = SecurityContextHolder.getContext().getAuthentication();

            // Cek apakah user punya salah satu dari hak akses sakti ini
            boolean isAllowed = auth.getAuthorities().stream()
                    .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN")
                            || a.getAuthority().equals("ROLE_MANAGER")
                            || a.getAuthority().equals("TRANSACTION_VOID")); // Permission Khusus

            if (!isAllowed) {
                // Lempar error agar ditangkap GlobalExceptionHandler (403 Forbidden)
                throw new AccessDeniedException("Anda tidak memiliki izin membatalkan transaksi!");
            }
        }

        notaService.updateStatus(id, newStatus);
        return ResponseEntity.ok(Map.of("message", "Status berhasil diupdate"));
    }

    // --- 7. DOWNLOAD PDF ---
    @GetMapping("/{id}/pdf")
    public ResponseEntity<byte[]> downloadPdf(@PathVariable Long id) throws IOException {
        NotaEntity nota = notaService.get(id);
        byte[] pdfBytes = pdfService.generateNotaPdf(nota);

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=Nota-" + nota.getKodeNota() + ".pdf")
                .contentType(MediaType.APPLICATION_PDF)
                .body(pdfBytes);
    }
}