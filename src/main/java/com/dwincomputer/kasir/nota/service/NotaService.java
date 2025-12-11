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
import java.util.List;

@Service
@RequiredArgsConstructor
public class NotaService {

    private final NotaRepository notaRepo;
    private final ItemRepository itemRepo;

    public List<NotaEntity> getAll() { return notaRepo.findAll(); }

    public NotaEntity get(Long id) {
        return notaRepo.findById(id).orElseThrow(() -> new RuntimeException("Nota tidak ditemukan"));
    }

    private String generateNomorSeri() {
        LocalDate today = LocalDate.now();
        String prefix = today.format(DateTimeFormatter.ofPattern("ddMMyy"));
        NotaEntity last = notaRepo.findLastByPrefix(prefix);
        int nextCounter = 1;
        if (last != null) {
            String counter = last.getNomorSeri().substring(6);
            nextCounter = Integer.parseInt(counter) + 1;
        }
        return prefix + String.format("%02d", nextCounter);
    }

    @Transactional
    public NotaEntity create(CreateNotaRequest req) {
        String nomorSeri = generateNomorSeri();
        NotaEntity nota = NotaEntity.builder()
                .nomorSeri(nomorSeri)
                .kodeNota("NT-" + nomorSeri)
                .tanggal(LocalDateTime.now())
                .kasirId(req.getKasirId())
                .kasirNama(req.getKasirNama())
                .customerNama(req.getCustomerNama())
                .customerTelp(req.getCustomerTelp())
                .customerAlamat(req.getCustomerAlamat())
                .tipe(req.getTipe())
                .status(req.getStatus())
                .barangCustomer(req.getBarangCustomer())
                .keluhan(req.getKeluhan())
                .dp(req.getDp() == null ? BigDecimal.ZERO : req.getDp())
                .build();

        processItems(nota, req.getItems());
        return notaRepo.save(nota);
    }

    @Transactional
    public NotaEntity update(Long id, CreateNotaRequest req) {
        NotaEntity nota = get(id);

        // 1. Return Stok Lama
        for (NotaSnapshotEntity oldItem : nota.getSnapshots()) {
            if (oldItem.getItemId() != null) {
                ItemEntity gudangItem = itemRepo.findById(oldItem.getItemId()).orElse(null);
                if (gudangItem != null) {
                    gudangItem.setStok(gudangItem.getStok() + oldItem.getJumlah());
                    itemRepo.save(gudangItem);
                }
            }
        }
        // 2. Clear Snapshot
        nota.getSnapshots().clear();

        // 3. Update Header
        nota.setCustomerNama(req.getCustomerNama());
        nota.setCustomerTelp(req.getCustomerTelp());
        nota.setCustomerAlamat(req.getCustomerAlamat());
        nota.setBarangCustomer(req.getBarangCustomer());
        nota.setKeluhan(req.getKeluhan());
        nota.setDp(req.getDp() == null ? BigDecimal.ZERO : req.getDp());
        // Status sebaiknya tidak diupdate via PUT full edit jika sudah ada tombol quick action,
        // tapi jika ingin tetap bisa, biarkan baris di bawah:
        // nota.setStatus(req.getStatus());

        // 4. Process Item Baru
        processItems(nota, req.getItems());

        return notaRepo.save(nota);
    }

    // --- HELPER CRITICAL: PROCESS ITEMS & STOCK & MODAL ---
    private void processItems(NotaEntity nota, List<CreateNotaRequest.CreateNotaItem> items) {
        BigDecimal total = BigDecimal.ZERO;

        if (items != null) {
            for (CreateNotaRequest.CreateNotaItem it : items) {
                BigDecimal subtotal = it.getHargaSatuan().multiply(BigDecimal.valueOf(it.getJumlah()));
                total = total.add(subtotal);

                BigDecimal modalTercatat = BigDecimal.ZERO; // Default 0 untuk Jasa

                // JIKA BARANG STOK (Ada ID)
                if (it.getItemId() != null) {
                    ItemEntity itemGudang = itemRepo.findById(it.getItemId())
                            .orElseThrow(() -> new RuntimeException("Barang ID " + it.getItemId() + " tidak ditemukan"));

                    // Cek Stok
                    if (itemGudang.getStok() < it.getJumlah()) {
                        throw new RuntimeException("Stok " + itemGudang.getNama() + " habis/kurang!");
                    }

                    // Ambil Modal dari Master Barang
                    if (itemGudang.getHargaBeli() != null) {
                        modalTercatat = itemGudang.getHargaBeli();
                    }

                    // Potong Stok
                    itemGudang.setStok(itemGudang.getStok() - it.getJumlah());
                    itemRepo.save(itemGudang);
                }

                NotaSnapshotEntity snap = NotaSnapshotEntity.builder()
                        .nota(nota)
                        .itemId(it.getItemId())
                        .namaBarang(it.getNamaBarang())
                        .catatan(it.getCatatan())
                        .hargaSatuan(it.getHargaSatuan())
                        .modal(modalTercatat) // SIMPAN MODAL DI SINI
                        .jumlah(it.getJumlah())
                        .totalHarga(subtotal)
                        .build();

                nota.getSnapshots().add(snap);
            }
        }
        nota.setTotal(total);
        nota.setSisa(total.subtract(nota.getDp()));
    }

    public void updateStatus(Long id, String newStatus) {
        NotaEntity nota = get(id);
        nota.setStatus(newStatus);
        notaRepo.save(nota);
    }
}