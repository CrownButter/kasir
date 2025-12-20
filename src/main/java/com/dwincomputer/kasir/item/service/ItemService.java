package com.dwincomputer.kasir.item.service;

import com.dwincomputer.kasir.item.entity.ItemEntity;
import com.dwincomputer.kasir.item.repository.ItemRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ItemService {

    private final ItemRepository repo;

    public ItemEntity save(ItemEntity item) {
        // 1. Validasi Input
        if (item.getKode() == null || item.getKode().length() < 3) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Kode produk minimal harus 3 karakter!");
        }
        if (item.getStok() == null || item.getStok() < 0) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Stok awal produk tidak boleh negatif!");
        }

        // 2. Cek Duplikasi untuk Data Baru
        if (item.getId() == null) {
            if (repo.existsByNama(item.getNama())) {
                throw new ResponseStatusException(HttpStatus.CONFLICT, "Nama item '" + item.getNama() + "' sudah terdaftar!");
            }
            if (repo.existsByKode(item.getKode())) {
                throw new ResponseStatusException(HttpStatus.CONFLICT, "Kode item '" + item.getKode() + "' sudah digunakan!");
            }
        }
        // 3. Cek Duplikasi untuk Update Data Lama
        else {
            if (repo.existsByNamaAndIdNot(item.getNama(), item.getId())) {
                throw new ResponseStatusException(HttpStatus.CONFLICT, "Nama item '" + item.getNama() + "' sudah digunakan oleh item lain!");
            }
            if (repo.existsByKodeAndIdNot(item.getKode(), item.getId())) {
                throw new ResponseStatusException(HttpStatus.CONFLICT, "Kode item '" + item.getKode() + "' sudah digunakan oleh item lain!");
            }
        }

        return repo.save(item);
    }

    public List<ItemEntity> all() {
        return repo.findAll();
    }

    // Method ini sekarang sudah berada di luar method all()
    public ItemEntity getByKode(String kode) {
        return repo.findByKode(kode).orElse(null);
    }

    public void delete(Long id) {
        repo.deleteById(id);
    }
}