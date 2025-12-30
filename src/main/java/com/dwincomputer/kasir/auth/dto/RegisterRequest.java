package com.dwincomputer.kasir.auth.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;
import jakarta.validation.constraints.Pattern;
import java.util.Set;

@Data
public class RegisterRequest {
    @NotBlank(message = "Username tidak boleh kosong")
    @Size(min = 4, max = 20, message = "Username harus antara 4-20 karakter")
    private String username;

    @NotBlank(message = "Password tidak boleh kosong")
    @Pattern(regexp = "^(?=.*[0-9])(?=.*[a-z]).*$",
            message = "Password harus mengandung kombinasi huruf dan angka")
    @Size(min = 8, message = "Password minimal 6 karakter")
    private String password;

    @NotBlank(message = "Role wajib diisi")
    private String role;
    private Set<String> permissions;
}