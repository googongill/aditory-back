package com.googongill.aditory.controller.dto.user.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class RefreshRequest {
    @NotNull
    private Long userId;
    @NotBlank
    private String refreshToken;
}
