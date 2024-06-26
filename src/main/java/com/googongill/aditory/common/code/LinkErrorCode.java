package com.googongill.aditory.common.code;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public enum LinkErrorCode implements BusinessErrorCode {

    /**
     * 400 Bad Request
     */
    LINK_NOT_IN_CATEGORY(HttpStatus.BAD_REQUEST, "해당 카테고리에 속해있지 않는 링크가 포함되어 있습니다."),

    /**
     * 401 Forbidden
     */
    LINK_FORBIDDEN(HttpStatus.FORBIDDEN, "접근할 수 없는 링크입니다."),

    /**
     * 404 Not Found
     */
    LINK_NOT_FOUND(HttpStatus.NOT_FOUND, "존재하지 않는 링크입니다."),
    REMINDER_EMPTY(HttpStatus.NOT_FOUND, "리마인더가 존재하지 않습니다."),

    ;

    private final HttpStatus httpStatus;
    private final String message;
}
