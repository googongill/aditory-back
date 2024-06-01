package com.googongill.aditory.security.oauth;

public interface OAuth2UserInfo {
    String getProviderId();
    String getProvider();
    String getNickname();
}
