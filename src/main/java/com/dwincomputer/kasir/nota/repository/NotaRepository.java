package com.dwincomputer.kasir.nota.repository;

import com.dwincomputer.kasir.nota.entity.NotaEntity;
import org.springframework.data.jpa.repository.JpaRepository; // Ganti ini
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface NotaRepository extends JpaRepository<NotaEntity, Long> { // Ganti CrudRepository ke JpaRepository

    @Query(value = """
        SELECT * FROM nota 
        WHERE nomor_seri LIKE CONCAT(:prefix, '%') 
        ORDER BY nomor_seri DESC 
        LIMIT 1
    """, nativeQuery = true)
    NotaEntity findLastByPrefix(String prefix);
}