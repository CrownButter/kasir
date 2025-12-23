package com.dwincomputer.kasir.auth;

import com.dwincomputer.kasir.auth.dto.RegisterRequest;
import com.dwincomputer.kasir.auth.dto.UserResponse;
import com.dwincomputer.kasir.auth.entity.User;
import com.dwincomputer.kasir.auth.repository.UserRepository;
import com.dwincomputer.kasir.auth.service.AuditService; // Import service audit
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;
import jakarta.validation.Valid;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/admin/users")
@RequiredArgsConstructor
@PreAuthorize("hasRole('ADMIN')") // Hanya Admin yang bisa akses 
public class AdminController {

    private final UserRepository userRepo; //
    private final PasswordEncoder encoder; // 
    private final AuditService auditService; // Tambahkan untuk logging

    // 1. LIST USERS
    @GetMapping
    public ResponseEntity<List<UserResponse>> listUsers() {
        List<User> users = userRepo.findAll(); 
        List<UserResponse> response = users.stream()
                .map(user -> UserResponse.builder()
                        .id(user.getId())
                        .username(user.getUsername())
                        .role(user.getRole())
                        .build()) 
                .collect(Collectors.toList());
        return ResponseEntity.ok(response); 
    }

    // 2. TAMBAH USER BARU
    @PostMapping
    public ResponseEntity<?> addUser(@Valid @RequestBody RegisterRequest req) {
        if (userRepo.findByUsername(req.getUsername()).isPresent()) {
            return ResponseEntity.badRequest().body("Username sudah digunakan!");
        }

        User user = User.builder()
                .username(req.getUsername())
                .password(encoder.encode(req.getPassword()))
                .role(req.getRole() == null || req.getRole().isEmpty() ? "KASIR" : req.getRole().toUpperCase())
                .build(); 
        userRepo.save(user);
        auditService.log("CREATE_USER", "Admin menambah user baru: " + user.getUsername());
        return ResponseEntity.ok("User berhasil ditambahkan"); 
    }

    // 3. UPDATE USER
    @PutMapping("/{id}")
    public ResponseEntity<?> updateUser(@PathVariable Long id, @RequestBody RegisterRequest req) {
        User user = userRepo.findById(id)
                .orElseThrow(() -> new RuntimeException("User tidak ditemukan"));

        if (req.getUsername() != null && !req.getUsername().isEmpty()) {
            if (!user.getUsername().equals(req.getUsername()) &&
                    userRepo.findByUsername(req.getUsername()).isPresent()) {
                return ResponseEntity.badRequest().body("Username sudah dipakai user lain!"); 
            }
            user.setUsername(req.getUsername()); 
        }

        if (req.getPassword() != null && !req.getPassword().isEmpty()) {
            user.setPassword(encoder.encode(req.getPassword()));
        }

        if (req.getRole() != null && !req.getRole().isEmpty()) {
            String currentUsername = SecurityContextHolder.getContext().getAuthentication().getName();
            if (user.getUsername().equals(currentUsername) && !req.getRole().equals("ADMIN")) {
                return ResponseEntity.badRequest().body("Anda tidak bisa mengubah role Anda sendiri!");
            }
            user.setRole(req.getRole()); 
        }

        userRepo.save(user); 
        auditService.log("UPDATE_USER", "Admin memperbarui user: " + user.getUsername());
        return ResponseEntity.ok("User berhasil diupdate"); 
    }

    // 4. HAPUS USER
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteUser(@PathVariable Long id) {
        User user = userRepo.findById(id)
                .orElseThrow(() -> new RuntimeException("User tidak ditemukan"));

        if (user.getId() == 1L) {
            return ResponseEntity.badRequest().body("Tidak bisa menghapus SUPER ADMIN!");
        }

        String currentUsername = SecurityContextHolder.getContext().getAuthentication().getName();
        if (user.getUsername().equals(currentUsername)) {
            return ResponseEntity.badRequest().body("Anda tidak bisa menghapus akun sendiri!");
        }

        String deletedUser = user.getUsername();
        userRepo.delete(user); 
        auditService.log("DELETE_USER", "Admin menghapus user: " + deletedUser);
        return ResponseEntity.ok("User berhasil dihapus"); 
    }
}