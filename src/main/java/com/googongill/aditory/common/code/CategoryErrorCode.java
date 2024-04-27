package com.googongill.aditory.common.code;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public enum CategoryErrorCode implements BusinessErrorCode {

    /**
     * 401 Forbidden
     */
    FORBIDDEN_CATEGORY(HttpStatus.FORBIDDEN, "추가할 수 없는 카테고리입니다."),

    /**
     * 404 Not Found
     */
    CATEGORY_NOT_FOUND(HttpStatus.NOT_FOUND, "존재하지 않는 카테고리입니다."),


    ;

    private final HttpStatus httpStatus;
    private final String message;
}