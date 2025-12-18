package com.dwincomputer.kasir.toko.controller;

import com.dwincomputer.kasir.toko.entity.TokoEntity;
import com.dwincomputer.kasir.toko.service.TokoService;
import com.dwincomputer.kasir.service.FileStorageService;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@RestController
@RequestMapping("/api/admin/toko")
@RequiredArgsConstructor
public class TokoController {

    private final TokoService tokoService;
    private final FileStorageService fileStorageService;

    @GetMapping
    public ResponseEntity<?> get() {
        return ResponseEntity.ok(tokoService.get());
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<?> save(
            @RequestPart("toko") String tokoJson,
            @RequestPart(value = "logo", required = false) MultipartFile logo) throws IOException {

        ObjectMapper objectMapper = new ObjectMapper();
        TokoEntity toko = objectMapper.readValue(tokoJson, TokoEntity.class);

        if (logo != null && !logo.isEmpty()) {
            String url = fileStorageService.storeFile(logo, "logo");
            toko.setLogoUrl(url); // Simpan path URL logo ke database
        } else {
            // Jika tidak mengunggah logo baru, pertahankan URL logo yang lama
            TokoEntity currentToko = tokoService.get();
            toko.setLogoUrl(currentToko.getLogoUrl());
        }

        return ResponseEntity.ok(tokoService.save(toko));
    }
}