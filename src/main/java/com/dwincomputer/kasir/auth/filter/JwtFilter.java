package com.dwincomputer.kasir.auth.filter;

import com.dwincomputer.kasir.auth.service.JwtService;
import com.dwincomputer.kasir.auth.service.UserService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
@RequiredArgsConstructor
public class JwtFilter extends OncePerRequestFilter {

    private final JwtService jwtService;
    private final UserService userService;

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain)
            throws ServletException, IOException {

        final String authHeader = request.getHeader("Authorization");
        final String jwt;
        final String username;

        // DEBUG LOG 1
        System.out.println(">>> JWT FILTER TRIGGERED untuk URL: " + request.getRequestURI());

        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            System.out.println(">>> TOKEN TIDAK DITEMUKAN ATAU FORMAT SALAH");
            filterChain.doFilter(request, response);
            return;
        }

        try {
            jwt = authHeader.substring(7);
            username = jwtService.extractUsername(jwt);
            System.out.println(">>> USERNAME DARI TOKEN: " + username);

            if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {

                // Cek apakah user ada di DB
                UserDetails userDetails = this.userService.loadUserByUsername(username);
                System.out.println(">>> USER DITEMUKAN DI DB: " + userDetails.getUsername());

                // Validasi Token
                if (jwtService.validateToken(jwt, userDetails)) {
                    UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(
                            userDetails,
                            null,
                            userDetails.getAuthorities()
                    );
                    authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                    SecurityContextHolder.getContext().setAuthentication(authToken);
                    System.out.println(">>> AUTENTIKASI SUKSES! SecurityContext di-set.");
                } else {
                    System.out.println(">>> VALIDASI TOKEN GAGAL (Mungkin expired atau secret key beda)");
                }
            }
        } catch (Exception e) {
            System.out.println(">>> ERROR DI JWT FILTER: " + e.getMessage());
            e.printStackTrace(); // Agar kita lihat error lengkapnya
        }

        filterChain.doFilter(request, response);
    }
}