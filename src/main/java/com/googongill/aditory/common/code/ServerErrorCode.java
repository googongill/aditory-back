package com.googongill.aditory.common.code;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public enum ServerErrorCode implements BusinessErrorCode {

    /**
     * 500 Internal Server Error
     */
    INTERNAL_SERVER_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "알 수 없는 서버 에러가 발생했습니다"),
    INDEX_OUT_OF_BOUND_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "인덱스 범위를 넘어선 값입니다."),
    NULL_POINTER_ACCESS_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "널 포인터에 접근했습니다."),

    ;

    private final HttpStatus httpStatus;
    private final String message;
}
