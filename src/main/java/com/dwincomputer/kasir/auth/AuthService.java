package com.dwincomputer.kasir.auth;

import com.dwincomputer.kasir.auth.dto.*;
import com.dwincomputer.kasir.auth.entity.RefreshToken;
import com.dwincomputer.kasir.auth.entity.User;
import com.dwincomputer.kasir.auth.repository.RefreshTokenRepository;
import com.dwincomputer.kasir.auth.repository.UserRepository;
import com.dwincomputer.kasir.auth.service.JwtService;
import com.dwincomputer.kasir.auth.service.UserService;
import com.dwincomputer.kasir.auth.service.AuditService;
import com.dwincomputer.kasir.auth.service.LoginAttemptService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import java.util.Set;
import java.time.Instant;
import java.util.Optional;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepo;
    private final RefreshTokenRepository refreshTokenRepo;
    private final PasswordEncoder encoder;
    private final AuthenticationManager authManager;
    private final JwtService jwtService;
    private final UserService userService;
    private final AuditService auditService;
    private final LoginAttemptService loginAttemptService;

    @Value("${app.jwt.refresh-expiration-ms}")
    private Long refreshTokenDurationMs;

    public UserResponse register(RegisterRequest req) {
        if (userRepo.findByUsername(req.getUsername()).isPresent()) {
            throw new RuntimeException("Username '" + req.getUsername() + "' sudah digunakan!");
        }
        User user = User.builder()
                .username(req.getUsername())
                .password(encoder.encode(req.getPassword()))
                .role(req.getRole())
                .customPermissions(req.getPermissions())
                .build();
        userRepo.save(user);      // Audit Log: Mencatat registrasi user baru
        auditService.log("REGISTER", "User baru terdaftar: " + user.getUsername());

        return UserResponse.builder()
                .id(user.getId())
                .username(user.getUsername())
                .role(user.getRole())
                .permissions(user.getCustomPermissions())
                .build();
    }

    @Transactional
    public LoginResponse login(LoginRequest req) {
        // 1. Keamanan: Cek apakah user sedang diblokir (Rate Limiting)
        if (loginAttemptService.isBlocked(req.getUsername())) {
            throw new RuntimeException("Terlalu banyak percobaan kegagalan. Akun diblokir sementara (15 menit).");
        }

        try {
            // 2. Authenticate
            authManager.authenticate(new UsernamePasswordAuthenticationToken(req.getUsername(), req.getPassword())); 

            // Jika berhasil, reset hitungan percobaan gagal
            loginAttemptService.loginSucceeded(req.getUsername());

            User user = userRepo.findByUsername(req.getUsername())
                    .orElseThrow(() -> new RuntimeException("User not found")); 

            // 3. Buat Refresh Token (Single Session Enforcement) ]
            RefreshToken refreshToken = createRefreshToken(user.getId());

            // 4. Generate Access Token
            String accessToken = jwtService.generateToken(user, refreshToken.getToken()); 

            // Audit Log: Mencatat login berhasil
            auditService.log("LOGIN_SUCCESS", "User login: " + user.getUsername());

            return LoginResponse.builder()
                    .accessToken(accessToken)
                    .refreshToken(refreshToken.getToken())
                    .username(user.getUsername())
                    .role(user.getRole())
                    .permissions(user.getCustomPermissions())
                    .build();

        } catch (Exception e) {
            // Jika gagal, catat kegagalan untuk rate limiting
            loginAttemptService.loginFailed(req.getUsername());
            log.warn("Login gagal untuk user: {}", req.getUsername());
            throw e;
        }
    }

    // Logic Overwrite (Update existing, jangan delete)
    public RefreshToken createRefreshToken(Long userId) {
        User user = userRepo.findById(userId).get();
        Optional<RefreshToken> existingToken = refreshTokenRepo.findByUser(user); 
        RefreshToken refreshToken;

        if (existingToken.isPresent()) {
            refreshToken = existingToken.get();
            refreshToken.setToken(UUID.randomUUID().toString());
            refreshToken.setExpiryDate(Instant.now().plusMillis(refreshTokenDurationMs)); 
        } else {
            refreshToken = RefreshToken.builder()
                    .user(user)
                    .expiryDate(Instant.now().plusMillis(refreshTokenDurationMs))
                    .token(UUID.randomUUID().toString())
                    .build(); 
        }
        return refreshTokenRepo.save(refreshToken);  }

    public TokenRefreshResponse refreshToken(TokenRefreshRequest request) {
        String requestRefreshToken = request.getRefreshToken();
        return findByToken(requestRefreshToken)
                .map(this::verifyExpiration)              .map(RefreshToken::getUser)
                .map(user -> {
                    String token = jwtService.generateToken(user, requestRefreshToken);                  return TokenRefreshResponse.builder()
                            .accessToken(token)
                            .refreshToken(requestRefreshToken)
                            .build(); 
                })
                .orElseThrow(() -> new RuntimeException("Refresh token tidak valid atau sudah expired!")); 
    }

    public Optional<RefreshToken> findByToken(String token) {
        return refreshTokenRepo.findByToken(token);  }

    public RefreshToken verifyExpiration(RefreshToken token) {
        if (token.getExpiryDate().compareTo(Instant.now()) < 0) {
            refreshTokenRepo.delete(token);
            throw new RuntimeException("Refresh token expired. Silakan login ulang.");
        }
        return token;
    }

    @Transactional
    public void logout() {
        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal(); 
        if (principal instanceof User) {
            User user = (User) principal;
            refreshTokenRepo.deleteByUser(user);          // Audit Log: Mencatat logout
            auditService.log("LOGOUT", "User logout: " + user.getUsername());
        }
    }
}