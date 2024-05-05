package com.googongill.aditory.common.code;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public enum CommonErrorCode implements BusinessErrorCode {

    /**
     * 400 Bad Request
     */
    REQUEST_INVALID(HttpStatus.BAD_REQUEST, "형식적 유효성 검사에 올바르지 않는 요청 값입니다"),
    HEADER_REQUEST_MISSING(HttpStatus.BAD_REQUEST, "요청에 필요한 헤더값이 존재하지 않습니다."),
    HTTP_REQUEST_INVALID(HttpStatus.BAD_REQUEST, "허용되지 않는 문자열이 입력되었습니다."),
    IMAGE_EXTENSION_UNSUPPORTED(HttpStatus.BAD_REQUEST, "이미지 확장자는 jpg, png, webp만 가능합니다."),
    IMAGE_SIZE_UNSUPPORTED(HttpStatus.BAD_REQUEST, "이미지 사이즈는 5MB를 넘을 수 없습니다."),
    BIND_EXCEPTION(HttpStatus.BAD_REQUEST, "Spring Bean Validation 에러입니다."),

    /**
     * 401 Unauthorized
     */
    AUTHENTICATE_JWT_FAIL(HttpStatus.UNAUTHORIZED, "내부 인증 필터를 거치는 데 실패한 액세스 토큰입니다"),

    /**
     * 405 Method Not Allowed
     */
    HTTP_METHOD_INVALID(HttpStatus.METHOD_NOT_ALLOWED, "지원되지 않는 HTTP Method 요청입니다."),

    /**
     * 500 Internal Server Error
     */
    GET_IMAGE_TO_S3_BUCKET_FAIL(HttpStatus.INTERNAL_SERVER_ERROR, "S3에서 이미지를 불러오는 데에 실패했습니다."),

    ;

    private final HttpStatus httpStatus;
    private final String message;
}
