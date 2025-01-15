package com.googongill.aditory.controller.dto.user.request

import jakarta.validation.constraints.NotBlank

data class UpdateUserRequest (
    @NotBlank
    val nickname: String,
    val contact: String? = null
)
