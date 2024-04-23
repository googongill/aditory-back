package com.googongill.aditory.controller.dto.user;

import jakarta.validation.constraints.NotBlank;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class RefreshRequest {
    @NotBlank
    private String refreshToken;
}
