package com.dwincomputer.kasir.auth;

import com.dwincomputer.kasir.auth.dto.*;
import com.dwincomputer.kasir.auth.entity.User;
import com.dwincomputer.kasir.auth.repository.UserRepository;
import com.dwincomputer.kasir.auth.service.JwtService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepo;
    private final PasswordEncoder encoder;
    private final AuthenticationManager authManager;
    private final JwtService jwtService;

    public UserResponse register(RegisterRequest req) {
        // === TAMBAHKAN VALIDASI INI ===
        if (userRepo.findByUsername(req.getUsername()).isPresent()) {
            throw new RuntimeException("Username '" + req.getUsername() + "' sudah digunakan!");
        }
        // ==============================

        User user = User.builder()
                .username(req.getUsername())
                .password(encoder.encode(req.getPassword()))
                .role(req.getRole())
                .build();

        userRepo.save(user);

        return UserResponse.builder()
                .id(user.getId())
                .username(user.getUsername())
                .role(user.getRole())
                .build();
    }

    public LoginResponse login(LoginRequest req) {

        authManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        req.getUsername(),
                        req.getPassword()
                )
        );

        User user = userRepo.findByUsername(req.getUsername())
                .orElseThrow(() -> new RuntimeException("User not found"));

        String token = jwtService.generateToken(user);

        return LoginResponse.builder()
                .token(token)
                .username(user.getUsername())
                .role(user.getRole())
                .build();
    }
}
