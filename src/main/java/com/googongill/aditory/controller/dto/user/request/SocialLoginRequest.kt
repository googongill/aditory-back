package com.googongill.aditory.controller.dto.user.request

data class SocialLoginRequest (
    val provider: String,
    val code: String
)
