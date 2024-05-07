package com.googongill.aditory.common.code;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public enum UserErrorCode implements BusinessErrorCode {

    /**
     * 400 Bad Request
     */
    PASSWORD_INVALID(HttpStatus.BAD_REQUEST, "비밀번호가 일치하지 않습니다."),
    SOCIAL_PLATFORM_INVALID(HttpStatus.BAD_REQUEST, "유효하지 않은 소셜 플랫폼입니다."),
    ALREADY_WITHDRAW_USER(HttpStatus.BAD_REQUEST, "이미 탈퇴한 사용자입니다."),

    /**
     * 401 Unauthorized
     */
    TOKEN_INVALID(HttpStatus.UNAUTHORIZED, "액세스 토큰이 비어있거나, 유효하지 않은 엑세스 토큰입니다."),
    TOKEN_UNSUPPORTED(HttpStatus.UNAUTHORIZED, "지원되지 않는 토큰입니다."),
    TOKEN_EXPIRED(HttpStatus.UNAUTHORIZED, "만료된 토큰입니다."),
    TOKEN_NOT_FOUND(HttpStatus.UNAUTHORIZED, "토큰을 찾을 수 없습니다."),
    TOKEN_MISSING_USERNAME(HttpStatus.UNAUTHORIZED, "토큰이 사용자 아이디를 담고있지 않습니다."),

    /**
     * 404 Not Found
     */
    USER_NOT_FOUND(HttpStatus.NOT_FOUND, "존재하지 않는 사용자입니다."),
    PROFILE_IMAGE_NOT_FOUND(HttpStatus.NOT_FOUND, "프로필 사진이 존재하지 않는 사용자입니다."),

    /**
     * 409 Conflict
     */
    ALREADY_EXISTING_USERNAME(HttpStatus.CONFLICT, "이미 존재하는 아이디입니다."),

    ;

    private final HttpStatus httpStatus;
    private final String message;
}
