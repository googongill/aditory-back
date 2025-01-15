package com.googongill.aditory.service.dto.user

data class OAuthToken (
    val token_type: String,
    val access_token: String,
    val expires_in: Int,
    val refresh_token: String,
    val refresh_token_expires_in: Int,
    val scope: String
)
