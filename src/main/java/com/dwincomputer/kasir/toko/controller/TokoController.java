package com.dwincomputer.kasir.toko;

import com.dwincomputer.kasir.toko.entity.TokoEntity;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/admin/toko") // Endpoint khusus admin
@RequiredArgsConstructor
public class TokoController {

    private final TokoService tokoService;

    @GetMapping
    public ResponseEntity<?> get() {
        return ResponseEntity.ok(tokoService.get());
    }

    @PostMapping
    public ResponseEntity<?> save(@RequestBody TokoEntity req) {
        return ResponseEntity.ok(tokoService.save(req));
    }
}