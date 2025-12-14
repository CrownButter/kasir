package com.dwincomputer.kasir.item.controller;

import com.dwincomputer.kasir.item.entity.ItemEntity;
import com.dwincomputer.kasir.item.service.ItemService;
import jakarta.validation.Valid; // Tambahkan import ini
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/items")
@RequiredArgsConstructor
public class ItemController {

    private final ItemService service;

    @GetMapping
    public List<ItemEntity> list() { return service.all(); }

    @PostMapping
    // Tambahkan @Valid di sini
    public ItemEntity save(@Valid @RequestBody ItemEntity item) {
        return service.save(item);
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable Long id) {
        service.delete(id);
    }
}