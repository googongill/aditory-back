package com.googongill.aditory.controller.dto.link.request

import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.NotNull

data class UpdateLinkRequest (
    @NotBlank
    val title: String,
    @NotBlank
    val summary: String,
    @NotBlank
    val url: String,
    @NotNull
    val categoryId: Long
)
