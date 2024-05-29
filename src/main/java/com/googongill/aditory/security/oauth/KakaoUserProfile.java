package com.googongill.aditory.security.oauth;

import lombok.Getter;

import java.util.Map;

@Getter
public class KakaoUserProfile implements OAuth2UserInfo {

    private Map<String, Object> attributes;

    public KakaoUserProfile(Map<String, Object> attributes) {
        this.attributes = attributes;
    }

    @Override
    public String getProviderId() {
        return String.valueOf(attributes.get("sub"));
    }

    @Override
    public String getProvider() {
        return "kakao";
    }

    @Override
    public String getNickname() {
        return String.valueOf(attributes.get("nickname"));
    }
}
