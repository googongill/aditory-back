package com.googongill.aditory.common.code;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public enum AWSS3ErrorCode implements BusinessErrorCode {

    /**
     * 400 Bad Request
     */
    BUCKET_PREFIX_INVALID(HttpStatus.BAD_REQUEST, "유효하지 않는 S3 버킷 디렉터리 이름입니다."),

    /**
     * 404 Not Found
     */
    IMAGE_NOT_FOUND(HttpStatus.NOT_FOUND, "해당 이름의 이미지가 버킷에 존재하지 않습니다."),

    /**
     * 500 Internal Server Error
     */
    UPLOAD_IMAGE_FAIL(HttpStatus.INTERNAL_SERVER_ERROR, "S3 버킷에 이미지를 업로드하는 데 실패했습니다."),
    DELETE_IMAGE_FAIL(HttpStatus.INTERNAL_SERVER_ERROR, "S3 버킷으로부터 이미지를 삭제하는 데 실패했습니다.")

    ;

    private final HttpStatus httpStatus;
    private final String message;
}
