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

    // === TAMBAHAN BARU: AMBIL SEMUA DATA ===
    public List<NotaEntity> getAll() {
        return notaRepo.findAll();
    }
    // =======================================

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
                .customerAlamat(req.getCustomerAlamat())
                .customerTelp(req.getCustomerTelp())
                .tipe(req.getTipe())
                .status(req.getStatus())
                .barangCustomer(req.getBarangCustomer())
                .keluhan(req.getKeluhan())
                .dp(req.getDp() == null ? BigDecimal.ZERO : req.getDp())
                .build();

        BigDecimal total = BigDecimal.ZERO;

        if (req.getItems() != null) {
            for (CreateNotaRequest.CreateNotaItem it : req.getItems()) {
                BigDecimal subtotal = it.getHargaSatuan().multiply(BigDecimal.valueOf(it.getJumlah()));
                total = total.add(subtotal);

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

                if (it.getItemId() != null) {
                    ItemEntity itemGudang = itemRepo.findById(it.getItemId())
                            .orElseThrow(() -> new RuntimeException("Barang ID " + it.getItemId() + " tidak ditemukan"));
                    if (itemGudang.getStok() < it.getJumlah()) {
                        throw new RuntimeException("Stok " + itemGudang.getNama() + " habis!");
                    }
                    itemGudang.setStok(itemGudang.getStok() - it.getJumlah());
                    itemRepo.save(itemGudang);
                }
            }
        }

        nota.setTotal(total);
        nota.setSisa(total.subtract(nota.getDp()));

        return notaRepo.save(nota);
    }

    public NotaEntity get(Long id) {
        return notaRepo.findById(id).orElseThrow(() -> new RuntimeException("Nota tidak ditemukan"));
    }
    public void updateStatus(Long id, String newStatus) {
        NotaEntity nota = get(id); // pakai method get yg sudah ada
        nota.setStatus(newStatus);
        notaRepo.save(nota);
    }
}