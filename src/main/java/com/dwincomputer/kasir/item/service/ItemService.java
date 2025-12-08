package com.dwincomputer.kasir.item.service;

import com.dwincomputer.kasir.item.entity.ItemEntity;
import com.dwincomputer.kasir.item.repository.ItemRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ItemService {

    private final ItemRepository repo;

    public List<ItemEntity> all() { return repo.findAll(); }

    public ItemEntity save(ItemEntity item) { return repo.save(item); }

    public void delete(Long id) { repo.deleteById(id); }
}
