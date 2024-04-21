package com.googongill.aditory.controller.dto.user;

import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class LoginRequest {
    @NotNull
    private String username;
    @NotNull
    private String password;
}
