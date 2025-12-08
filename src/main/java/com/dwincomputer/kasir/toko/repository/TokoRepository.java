package com.dwincomputer.kasir.toko.repository;

import com.dwincomputer.kasir.toko.entity.TokoEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TokoRepository extends JpaRepository<TokoEntity, Long> {
}