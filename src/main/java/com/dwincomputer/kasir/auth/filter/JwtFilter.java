package com.dwincomputer.kasir.auth.filter;

import com.dwincomputer.kasir.auth.entity.RefreshToken;
import com.dwincomputer.kasir.auth.repository.RefreshTokenRepository;
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
    private final RefreshTokenRepository refreshTokenRepo; // [BARU] Inject Repo

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain)
            throws ServletException, IOException {

        final String authHeader = request.getHeader("Authorization");
        final String jwt;
        final String username;

        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            filterChain.doFilter(request, response);
            return;
        }

        try {
            jwt = authHeader.substring(7);
            username = jwtService.extractUsername(jwt);

            if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {
                UserDetails userDetails = this.userService.loadUserByUsername(username);

                //  SECURITY CHECK REAL-TIME ---
                // 1. Ambil ID yang tertanam di Access Token user
                String ridInToken = jwtService.extractRefreshTokenId(jwt);

                // 2. Ambil Refresh Token User yang AKTIF di Database
                com.dwincomputer.kasir.auth.entity.User userEntity =
                        (com.dwincomputer.kasir.auth.entity.User) userDetails;

                RefreshToken dbRefreshToken = refreshTokenRepo.findByUser(userEntity).orElse(null);

                // 3. PENOLAKAN KERAS (Hard Reject)
                // Jika di DB kosong (terhapus manual) ATAU string token beda (ditimpa user lain)
                if (dbRefreshToken == null || !dbRefreshToken.getToken().equals(ridInToken)) {
                    response.setStatus(HttpServletResponse.SC_UNAUTHORIZED); // 401
                    response.getWriter().write("Sesi kadaluarsa atau login di device lain.");
                    return; // Stop request, jangan lanjut ke Controller
                }
                // ---------------------------------------------

                if (jwtService.validateToken(jwt, userDetails)) {
                    UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(
                            userDetails, null, userDetails.getAuthorities()
                    );
                    authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                    SecurityContextHolder.getContext().setAuthentication(authToken);
                }
            }
        } catch (Exception e) {
            System.out.println(">>> ERROR JWT FILTER: " + e.getMessage());
        }

        filterChain.doFilter(request, response);
    }
}