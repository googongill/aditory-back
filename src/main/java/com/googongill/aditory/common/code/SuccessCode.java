package com.googongill.aditory.common.code;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public enum SuccessCode {

    /**
     * 200 OK
     */
    // User
    LOGIN_SUCCESS(HttpStatus.OK, "로그인에 성공했습니다."),
    LOGOUT_SUCCESS(HttpStatus.OK, "로그아웃에 성공했습니다."),
    REFRESH_SUCCESS(HttpStatus.OK, "토큰 재발급에 성공했습니다."),

    /**
     * 201 Created
     */
    // User
    SIGNUP_SUCCESS(HttpStatus.CREATED, "회원가입에 성공했습니다."),

    ;

    private final HttpStatus httpStatus;
    private final String message;
}
