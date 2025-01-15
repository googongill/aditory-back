package com.googongill.aditory.security.jwt.dto

data class JwtResult (
    val accessToken: String? = null,
    val refreshToken: String? = null
)
