package com.dwincomputer.kasir.auth.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class TokenRefreshRequest {
    @NotBlank(message = "Refresh token wajib diisi")
    private String refreshToken;
}