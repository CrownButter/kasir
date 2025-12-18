package com.dwincomputer.kasir.item.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import com.dwincomputer.kasir.item.entity.ItemEntity;
import java.util.Optional;

public interface ItemRepository extends JpaRepository<ItemEntity, Long> {
    // Method untuk mengecek keberadaan nama dan kode
    boolean existsByNama(String nama);
    boolean existsByKode(String kode);

    // Opsional: Untuk keperluan update (mengecek nama yang sama tapi ID berbeda)
    boolean existsByNamaAndIdNot(String nama, Long id);
    boolean existsByKodeAndIdNot(String kode, Long id);
}