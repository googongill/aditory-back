package com.googongill.aditory.common.code;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public enum CategoryErrorCode implements BusinessErrorCode {

    /**
     * 400 Bad Request
     */
    IMPORT_FILE_PARSE_FAIL(HttpStatus.BAD_REQUEST, "파일 파싱 중 오류가 발생했습니다."),

    /**
     * 401 Forbidden
     */
    CATEGORY_FORBIDDEN(HttpStatus.FORBIDDEN, "접근할 수 없는 카테고리입니다."),

    /**
     * 404 Not Found
     */
    CATEGORY_NOT_FOUND(HttpStatus.NOT_FOUND, "존재하지 않는 카테고리입니다."),
    CATEGORY_NOT_LIKED(HttpStatus.NOT_FOUND, "좋아요하지 않은 카테고리입니다."),

    /**
     * 409 Conflict
     */
    CATEGORY_ALREADY_LIKED(HttpStatus.CONFLICT, "이미 좋아요한 카테고리입니다."),
    CATEGORY_LIMIT_EXCEEDED(HttpStatus.CONFLICT, "카테고리는 최대 30개까지 추가할 수 있습니다."),
    CATEGORY_ALREADY_OWNED(HttpStatus.CONFLICT, "이미 소유하고 있는 카테고리입니다."),

    ;

    private final HttpStatus httpStatus;
    private final String message;
}
