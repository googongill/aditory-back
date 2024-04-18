package com.googongill.aditory.exception;

import com.googongill.aditory.exception.code.BusinessErrorCode;
import com.googongill.aditory.exception.code.ErrorCode;
import org.springframework.http.HttpStatus;

public record ErrorResponse(
        HttpStatus status,
        String message
) {

        public static ErrorResponse of(ErrorCode errorCode) {
            return new ErrorResponse(
                errorCode.getHttpStatus(),
                errorCode.getMessage()
            );
        }

        public static ErrorResponse of(BusinessErrorCode exceptionType) {
            return new ErrorResponse(
                    exceptionType.getHttpStatus(),
                    exceptionType.getMessage()
            );
        }
}
