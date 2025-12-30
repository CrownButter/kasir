package com.dwincomputer.kasir.auth.filter;

import com.dwincomputer.kasir.auth.service.JwtService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService; // 1. Import ini
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
@RequiredArgsConstructor
@Slf4j
public class JwtFilter extends OncePerRequestFilter {

    private final JwtService jwtService;
    private final UserDetailsService userDetailsService; // 2. Inject UserDetailsService

    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain
    ) throws ServletException, IOException {

        final String authHeader = request.getHeader("Authorization");
        final String jwt;

        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            filterChain.doFilter(request, response);
            return;
        }

        try {
            jwt = authHeader.substring(7);

            // 3. Hanya ambil Username dari Token
            String username = jwtService.extractUsername(jwt);

            if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {

                // 4. LOAD USER DARI DB (Ini kuncinya!)
                // Alih-alih baca permission dari token, kita minta data segar dari Database.
                // Karena CustomUserDetailsService kita sudah pakai query "LEFT JOIN FETCH",
                // Permission yang diambil pasti yang terbaru.
                UserDetails userDetails = userDetailsService.loadUserByUsername(username);

                // 5. Validasi apakah tokennya sah milik user ini
                if (jwtService.validateToken(jwt, userDetails)) {

                    // 6. Set Authentication pakai data dari DB (userDetails.getAuthorities())
                    UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(
                            userDetails,
                            null,
                            userDetails.getAuthorities() // <-- Ini berisi Permission REAL-TIME dari DB
                    );

                    authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                    SecurityContextHolder.getContext().setAuthentication(authToken);
                }
            }
        } catch (Exception e) {
            log.error("JWT Error: {}", e.getMessage());
        }

        filterChain.doFilter(request, response);
    }
}