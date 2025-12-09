package com.dwincomputer.kasir.auth.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class RegisterRequest {
    @NotBlank(message = "Username tidak boleh kosong")
    @Size(min = 4, max = 20, message = "Username harus antara 4-20 karakter")
    private String username;

    @NotBlank(message = "Password tidak boleh kosong")
    @Size(min = 6, message = "Password minimal 6 karakter")
    private String password;

    @NotBlank(message = "Role wajib diisi")
    private String role;
}