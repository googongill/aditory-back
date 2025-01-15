package com.googongill.aditory.controller.dto.user.request

import jakarta.validation.constraints.NotBlank

data class LoginRequest (
    @NotBlank
    val username: String,
    @NotBlank
    val password: String
)
