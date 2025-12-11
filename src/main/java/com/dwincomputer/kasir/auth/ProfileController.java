package com.dwincomputer.kasir.auth;

import com.dwincomputer.kasir.auth.dto.UpdateProfileRequest;
import com.dwincomputer.kasir.auth.dto.UserResponse;
import com.dwincomputer.kasir.auth.entity.User;
import com.dwincomputer.kasir.auth.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/profile")
@RequiredArgsConstructor
public class ProfileController {

    private final UserRepository userRepo;
    private final PasswordEncoder encoder;

    // 1. LIHAT PROFIL SENDIRI
    @GetMapping
    public ResponseEntity<?> getMyProfile(Authentication authentication) {
        String username = authentication.getName();
        User user = userRepo.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User tidak ditemukan"));

        return ResponseEntity.ok(UserResponse.builder()
                .id(user.getId())
                .username(user.getUsername())
                .role(user.getRole())
                .build());
    }

    // 2. UPDATE PROFIL (Ganti Password / Username)
    @PutMapping
    public ResponseEntity<?> updateProfile(@RequestBody UpdateProfileRequest req, Authentication authentication) {
        String currentUsername = authentication.getName();
        User user = userRepo.findByUsername(currentUsername)
                .orElseThrow(() -> new RuntimeException("User tidak ditemukan"));

        // A. Validasi Password Lama (KEAMANAN)
        // User wajib memasukkan password lama sebelum mengganti data sensitif
        if (req.getOldPassword() == null || !encoder.matches(req.getOldPassword(), user.getPassword())) {
            return ResponseEntity.badRequest().body("Password lama salah!");
        }

        // B. Ganti Username (Cek duplikasi dulu)
        if (req.getUsername() != null && !req.getUsername().isEmpty() && !req.getUsername().equals(user.getUsername())) {
            if (userRepo.findByUsername(req.getUsername()).isPresent()) {
                return ResponseEntity.badRequest().body("Username sudah digunakan orang lain!");
            }
            user.setUsername(req.getUsername());
        }

        // C. Ganti Password Baru
        if (req.getNewPassword() != null && !req.getNewPassword().isEmpty()) {
            if (req.getNewPassword().length() < 6) {
                return ResponseEntity.badRequest().body("Password baru minimal 6 karakter");
            }
            user.setPassword(encoder.encode(req.getNewPassword()));
        }

        userRepo.save(user);
        return ResponseEntity.ok("Profil berhasil diperbarui");
    }
}