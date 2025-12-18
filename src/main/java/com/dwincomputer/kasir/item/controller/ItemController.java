package com.dwincomputer.kasir.item.controller;

import com.dwincomputer.kasir.item.entity.ItemEntity;
import com.dwincomputer.kasir.item.service.ItemService;
import com.dwincomputer.kasir.service.FileStorageService;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("/api/items")
@RequiredArgsConstructor
public class ItemController {

    private final ItemService service;
    private final FileStorageService fileStorageService;

    @GetMapping
    public List<ItemEntity> list() {
        return service.all();
    }

    // Menggunakan MULTIPART_FORM_DATA_VALUE agar bisa menerima File dan JSON sekaligus
    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ItemEntity save(
            @RequestPart("item") String itemJson,
            @RequestPart(value = "image", required = false) MultipartFile image) throws IOException {

        // Mengubah string JSON menjadi objek ItemEntity
        ObjectMapper objectMapper = new ObjectMapper();
        ItemEntity item = objectMapper.readValue(itemJson, ItemEntity.class);

        // Jika ada file gambar yang diunggah
        if (image != null && !image.isEmpty()) {
            String url = fileStorageService.storeFile(image, "item");
            item.setImageUrl(url); // Simpan path URL ke database
        }

        return service.save(item);
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable Long id) {
        service.delete(id);
    }
}