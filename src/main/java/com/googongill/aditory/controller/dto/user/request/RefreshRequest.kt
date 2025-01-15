package com.googongill.aditory.controller.dto.user.request

import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.NotNull

data class RefreshRequest (
    @NotNull
    val userId: Long,
    @NotBlank
    val refreshToken: String
)
