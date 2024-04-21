package com.googongill.aditory.service.dto;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class SignupResult {
    private Long userId;
    private String nickname;
}