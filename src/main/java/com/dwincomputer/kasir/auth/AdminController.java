package com.dwincomputer.kasir.auth;

import com.dwincomputer.kasir.auth.dto.RegisterRequest;
import com.dwincomputer.kasir.auth.entity.User;
import com.dwincomputer.kasir.auth.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize; // Import ini
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RestController
@RequestMapping("/api/admin/users")
@RequiredArgsConstructor
// KITA KUNCI SATU KELAS SEKALIGUS AGAR LEBIH AMAN
@PreAuthorize("hasRole('ADMIN')")
public class AdminController {

    private final UserRepository userRepo;
    private final PasswordEncoder encoder;

    @GetMapping
    public ResponseEntity<?> listUsers() {
        return ResponseEntity.ok(userRepo.findAll());
    }

    @PostMapping
    public ResponseEntity<?> addKasir(@RequestBody RegisterRequest req) {
        if (userRepo.findByUsername(req.getUsername()).isPresent()) {
            return ResponseEntity.badRequest().body("Username sudah digunakan!");
        }
        User user = User.builder()
                .username(req.getUsername())
                .password(encoder.encode(req.getPassword()))
                .role("KASIR")
                .build();
        userRepo.save(user);
        return ResponseEntity.ok("Kasir berhasil ditambahkan");
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteUser(@PathVariable Long id) {
        Optional<User> user = userRepo.findById(id);
        if (user.isPresent()) {
            if (user.get().getRole().equals("ADMIN")) {
                return ResponseEntity.badRequest().body("Tidak bisa menghapus ADMIN UTAMA!");
            }
            userRepo.deleteById(id);
            return ResponseEntity.ok("User berhasil dihapus");
        }
        return ResponseEntity.notFound().build();
    }
}