package com.googongill.aditory.service.dto.user;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class SignResult {
    private Long userId;
    private String nickname;
}