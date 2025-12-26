package com.dwincomputer.kasir.auth.entity;

import jakarta.persistence.*;
import lombok.*;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.List;

@Entity
@Table(name = "users")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class User implements UserDetails {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false)
    private String username;

    @Column(nullable = false)
    private String password;

    // --- BAGIAN INI YANG KURANG TADI ---
    @Column(name = "nama")
    private String nama;
    // -----------------------------------

    private String role; // Di database isinya: "ADMIN" atau "KASIR"

    // ============= USERDETAILS IMPLEMENTATION ============= //

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        // Otomatis menambahkan "ROLE_"
        // Jadi kalau di DB role="ADMIN", hasilnya jadi "ROLE_ADMIN" (Ini Benar)
        return List.of(new SimpleGrantedAuthority("ROLE_" + role));
    }

    @Override
    public String getPassword() {
        return password;
    }

    @Override
    public String getUsername() {
        return username;
    }

    @Override
    public boolean isAccountNonExpired() { return true; }

    @Override
    public boolean isAccountNonLocked() { return true; }

    @Override
    public boolean isCredentialsNonExpired() { return true; }

    @Override
    public boolean isEnabled() { return true; }
}