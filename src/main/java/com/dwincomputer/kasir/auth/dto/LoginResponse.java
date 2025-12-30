package com.dwincomputer.kasir.auth.dto;

import lombok.Builder;
import lombok.Data;
import java.util.Set;
@Data
@Builder
public class LoginResponse {
    private String accessToken;
    private String refreshToken;
    private String username;
    private String role;
    private Set<String> permissions;
}