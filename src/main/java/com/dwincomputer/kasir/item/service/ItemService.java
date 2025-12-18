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
        // 1. Cek jika ini adalah data baru (ID null)
        if (item.getId() == null) {
            if (repo.existsByNama(item.getNama())) {
                throw new ResponseStatusException(HttpStatus.CONFLICT, "Nama item '" + item.getNama() + "' sudah terdaftar!");
            }
            if (repo.existsByKode(item.getKode())) {
                throw new ResponseStatusException(HttpStatus.CONFLICT, "Kode item '" + item.getKode() + "' sudah digunakan!");
            }
        }
        // 2. Cek jika ini adalah update data lama
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

    public void delete(Long id) {
        repo.deleteById(id);
    }
}