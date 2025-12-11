package com.dwincomputer.kasir.auth.dto;

import lombok.Data;

@Data
public class UpdateProfileRequest {
    private String username;
    private String oldPassword;
    private String newPassword;
}