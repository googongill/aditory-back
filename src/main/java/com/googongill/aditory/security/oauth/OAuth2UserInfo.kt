package com.googongill.aditory.security.oauth

interface OAuth2UserInfo {
    fun getProviderId(): String
    fun getProvider(): String
    fun getNickname(): String
}
