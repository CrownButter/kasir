package com.dwincomputer.kasir.nota.repository;

import com.dwincomputer.kasir.nota.entity.NotaEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface NotaRepository extends JpaRepository<NotaEntity, Long> {


    List<NotaEntity> findByNomorSeriStartingWith(String prefix);

    @Query("SELECT n FROM NotaEntity n WHERE n.nomorSeri LIKE :prefix%")
    List<NotaEntity> searchByNomorSeriPrefix(@Param("prefix") String prefix);

    @Query(value = "SELECT * FROM nota WHERE nomor_seri LIKE CONCAT(:prefix, '%') ORDER BY nomor_seri DESC LIMIT 1", nativeQuery = true)
    NotaEntity findLastByPrefix(@Param("prefix") String prefix);
}