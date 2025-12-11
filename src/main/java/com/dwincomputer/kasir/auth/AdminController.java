package com.dwincomputer.kasir.auth;

import com.dwincomputer.kasir.auth.dto.RegisterRequest;
import com.dwincomputer.kasir.auth.dto.UserResponse;
import com.dwincomputer.kasir.auth.entity.User;
import com.dwincomputer.kasir.auth.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/admin/users")
@RequiredArgsConstructor
@PreAuthorize("hasRole('ADMIN')") // Hanya Admin yang bisa akses file ini
public class AdminController {

    private final UserRepository userRepo;
    private final PasswordEncoder encoder;

    // 1. LIST USERS (AMAN - Password disembunyikan)
    @GetMapping
    public ResponseEntity<List<UserResponse>> listUsers() {
        List<User> users = userRepo.findAll();

        // Convert Entity ke DTO agar password tidak bocor ke frontend
        List<UserResponse> response = users.stream()
                .map(user -> UserResponse.builder()
                        .id(user.getId())
                        .username(user.getUsername())
                        .role(user.getRole())
                        .build())
                .collect(Collectors.toList());

        return ResponseEntity.ok(response);
    }

    // 2. TAMBAH USER BARU (Bisa pilih Role)
    @PostMapping
    public ResponseEntity<?> addUser(@RequestBody RegisterRequest req) {
        if (userRepo.findByUsername(req.getUsername()).isPresent()) {
            return ResponseEntity.badRequest().body("Username sudah digunakan!");
        }

        User user = User.builder()
                .username(req.getUsername())
                .password(encoder.encode(req.getPassword()))
                // Jika role tidak dikirim, default jadi KASIR
                .role(req.getRole() == null || req.getRole().isEmpty() ? "KASIR" : req.getRole().toUpperCase())
                .build();

        userRepo.save(user);
        return ResponseEntity.ok("User berhasil ditambahkan");
    }

    // 3. UPDATE USER (Ganti Password / Username)
    @PutMapping("/{id}")
    public ResponseEntity<?> updateUser(@PathVariable Long id, @RequestBody RegisterRequest req) {
        User user = userRepo.findById(id)
                .orElseThrow(() -> new RuntimeException("User tidak ditemukan"));

        // Update Username (Cek duplikasi jika username berubah)
        if (req.getUsername() != null && !req.getUsername().isEmpty()) {
            if (!user.getUsername().equals(req.getUsername()) &&
                    userRepo.findByUsername(req.getUsername()).isPresent()) {
                return ResponseEntity.badRequest().body("Username sudah dipakai user lain!");
            }
            user.setUsername(req.getUsername());
        }

        // Update Password (Hanya jika diisi)
        if (req.getPassword() != null && !req.getPassword().isEmpty()) {
            user.setPassword(encoder.encode(req.getPassword()));
        }

        // Update Role (Opsional)
        if (req.getRole() != null && !req.getRole().isEmpty()) {
            // Cegah Admin menurunkan pangkat dirinya sendiri jadi Kasir (Bahaya)
            String currentUsername = SecurityContextHolder.getContext().getAuthentication().getName();
            if (user.getUsername().equals(currentUsername) && !req.getRole().equals("ADMIN")) {
                return ResponseEntity.badRequest().body("Anda tidak bisa mengubah role Anda sendiri!");
            }
            user.setRole(req.getRole());
        }

        userRepo.save(user);
        return ResponseEntity.ok("User berhasil diupdate");
    }

    // 4. HAPUS USER
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteUser(@PathVariable Long id) {
        User user = userRepo.findById(id)
                .orElseThrow(() -> new RuntimeException("User tidak ditemukan"));

        // Proteksi 1: Tidak boleh hapus ADMIN UTAMA (misal ID 1)
        if (user.getId() == 1L) {
            return ResponseEntity.badRequest().body("Tidak bisa menghapus SUPER ADMIN!");
        }

        // Proteksi 2: Tidak boleh menghapus DIRI SENDIRI yang sedang login
        String currentUsername = SecurityContextHolder.getContext().getAuthentication().getName();
        if (user.getUsername().equals(currentUsername)) {
            return ResponseEntity.badRequest().body("Anda tidak bisa menghapus akun sendiri saat sedang login!");
        }

        userRepo.delete(user);
        return ResponseEntity.ok("User berhasil dihapus");
    }
}