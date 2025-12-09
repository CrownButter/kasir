package com.dwincomputer.kasir.toko.service;

import com.dwincomputer.kasir.toko.entity.TokoEntity;
import com.dwincomputer.kasir.toko.repository.TokoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class TokoService {

    private final TokoRepository tokoRepo;

    // Ambil Data Toko (Auto create default jika kosong)
    public TokoEntity get() {
        return tokoRepo.findById(1L).orElseGet(() -> {
            TokoEntity def = new TokoEntity();
            def.setId(1L);
            def.setNamaToko("DWIN COMPUTER");
            def.setAlamatToko("Alamat Belum Diatur");
            def.setNoTelp("-");
            return tokoRepo.save(def);
        });
    }

    // Simpan Data Toko
    public TokoEntity save(TokoEntity req) {
        req.setId(1L); // Paksa ID jadi 1 agar cuma ada 1 toko
        return tokoRepo.save(req);
    }
}