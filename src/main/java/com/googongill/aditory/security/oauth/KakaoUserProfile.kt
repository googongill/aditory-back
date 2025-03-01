package com.googongill.aditory.security.oauth

data class KakaoUserProfile(val attributes: Map<String, Any>) : OAuth2UserInfo {
    override fun getProviderId(): String {
        return attributes["sub"].toString()
    }

    override fun getProvider(): String {
        return "kakao"
    }

    override fun getNickname(): String {
        return attributes["nickname"].toString()
    }

}
