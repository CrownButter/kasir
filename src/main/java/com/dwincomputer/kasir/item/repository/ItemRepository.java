package com.dwincomputer.kasir.item.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import com.dwincomputer.kasir.item.entity.ItemEntity;

public interface ItemRepository extends JpaRepository<ItemEntity, Long> { }
