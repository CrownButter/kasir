package com.dwincomputer.kasir.nota.service;

import com.dwincomputer.kasir.item.entity.ItemEntity;
import com.dwincomputer.kasir.item.repository.ItemRepository;
import com.dwincomputer.kasir.nota.dto.CreateNotaRequest;
import com.dwincomputer.kasir.nota.entity.NotaEntity;
import com.dwincomputer.kasir.nota.repository.NotaRepository;
import com.dwincomputer.kasir.nota.snapshot.NotaSnapshotEntity;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class NotaService {

    private final NotaRepository notaRepo;
    private final ItemRepository itemRepo; // Inject ItemRepository untuk stok

    // GENERATE NOMOR SERI (ddMMyy + 2 DIGIT COUNTER)
    private String generateNomorSeri() {
        LocalDate today = LocalDate.now();
        String prefix = today.format(DateTimeFormatter.ofPattern("ddMMyy"));

        NotaEntity last = notaRepo.findLastByPrefix(prefix);
        int nextCounter = 1;

        if (last != null) {
            String lastSeri = last.getNomorSeri();
            String counter = lastSeri.substring(6); // ambil setelah ddMMyy
            nextCounter = Integer.parseInt(counter) + 1;
        }

        return prefix + String.format("%02d", nextCounter);
    }

    // CREATE NOTA & KURANGI STOK
    @Transactional // Penting: Agar jika stok error, nota tidak tersimpan sebagian
    public NotaEntity create(CreateNotaRequest req) {
        String nomorSeri = generateNomorSeri();

        NotaEntity nota = NotaEntity.builder()
                .nomorSeri(nomorSeri)
                .kodeNota("NT-" + nomorSeri)
                .tanggal(LocalDateTime.now())
                .kasirId(req.getKasirId())
                .kasirNama(req.getKasirNama())
                .customerNama(req.getCustomerNama())
                .customerAlamat(req.getCustomerAlamat())
                .customerTelp(req.getCustomerTelp())
                .tipe(req.getTipe())
                .status(req.getStatus())
                .dp(req.getDp())
                .build();

        BigDecimal total = BigDecimal.ZERO;

        // Loop items untuk snapshot & update stok
        for (CreateNotaRequest.CreateNotaItem it : req.getItems()) {

            // 1. Hitung Subtotal
            BigDecimal subtotal = it.getHargaSatuan()
                    .multiply(BigDecimal.valueOf(it.getJumlah()));
            total = total.add(subtotal);

            // 2. Buat Snapshot (Audit Data)
            NotaSnapshotEntity snap = NotaSnapshotEntity.builder()
                    .nota(nota)
                    .itemId(it.getItemId())
                    .namaBarang(it.getNamaBarang())
                    .catatan(it.getCatatan())
                    .hargaSatuan(it.getHargaSatuan())
                    .jumlah(it.getJumlah())
                    .totalHarga(subtotal)
                    .build();

            nota.getSnapshots().add(snap);

            // 3. LOGIKA PENGURANGAN STOK (Hanya jika Item ID ada/bukan jasa manual)
            if (it.getItemId() != null && it.getItemId() > 0) {
                ItemEntity itemGudang = itemRepo.findById(it.getItemId())
                        .orElseThrow(() -> new RuntimeException("Barang dengan ID " + it.getItemId() + " tidak ditemukan"));

                // Cek Stok Cukup?
                if (itemGudang.getStok() < it.getJumlah()) {
                    throw new RuntimeException("Stok barang " + itemGudang.getNama() + " tidak cukup! Sisa: " + itemGudang.getStok());
                }

                // Kurangi Stok
                itemGudang.setStok(itemGudang.getStok() - it.getJumlah());
                itemRepo.save(itemGudang);
            }
        }

        nota.setTotal(total);
        BigDecimal dp = req.getDp() == null ? BigDecimal.ZERO : req.getDp();
        nota.setDp(dp);
        nota.setSisa(total.subtract(dp));

        return notaRepo.save(nota);
    }

    public NotaEntity get(Long id) {
        return notaRepo.findById(id).orElseThrow(() -> new RuntimeException("Nota tidak ditemukan"));
    }
}