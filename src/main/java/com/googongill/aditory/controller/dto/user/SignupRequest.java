package com.googongill.aditory.controller.dto.user;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SignupRequest {
    @NotNull
    private String username;
    @NotNull
    private String password;
    @NotNull
    private String nickname;
    private String contact;
}
