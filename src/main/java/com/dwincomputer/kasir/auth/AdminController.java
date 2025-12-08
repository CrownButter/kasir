package com.dwincomputer.kasir.auth;

import com.dwincomputer.kasir.auth.dto.RegisterRequest;
import com.dwincomputer.kasir.auth.entity.User;
import com.dwincomputer.kasir.auth.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RestController
@RequestMapping("/api/admin/users")
@RequiredArgsConstructor
public class AdminController {

    private final UserRepository userRepo;
    private final PasswordEncoder encoder;

    // 1. LIHAT SEMUA USER
    @GetMapping
    public ResponseEntity<?> listUsers() {
        return ResponseEntity.ok(userRepo.findAll());
    }

    // 2. TAMBAH KASIR BARU
    @PostMapping
    public ResponseEntity<?> addKasir(@RequestBody RegisterRequest req) {
        // Cek username kembar
        if (userRepo.findByUsername(req.getUsername()).isPresent()) {
            return ResponseEntity.badRequest().body("Username sudah digunakan!");
        }

        User user = User.builder()
                .username(req.getUsername())
                .password(encoder.encode(req.getPassword()))
                .role("KASIR") // Kita kunci role-nya jadi KASIR
                .build();

        userRepo.save(user);
        return ResponseEntity.ok("Kasir berhasil ditambahkan");
    }

    // 3. HAPUS USER (Kecuali Admin)
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteUser(@PathVariable Long id) {
        Optional<User> user = userRepo.findById(id);
        if (user.isPresent()) {
            if (user.get().getRole().equals("ADMIN")) {
                return ResponseEntity.badRequest().body("Tidak bisa menghapus ADMIN!");
            }
            userRepo.deleteById(id);
            return ResponseEntity.ok("User berhasil dihapus");
        }
        return ResponseEntity.notFound().build();
    }
}