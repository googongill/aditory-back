package com.googongill.aditory.exception;

import com.googongill.aditory.exception.code.BusinessErrorCode;
import com.googongill.aditory.exception.code.ErrorCode;

public record ErrorResponse(
        int code,
        String message
) {

        public static ErrorResponse of(ErrorCode errorCode) {
            return new ErrorResponse(
                errorCode.getHttpStatus().value(),
                errorCode.getMessage()
            );
        }

        public static ErrorResponse of(BusinessErrorCode exceptionType) {
            return new ErrorResponse(
                    exceptionType.getHttpStatus().value(),
                    exceptionType.getMessage()
            );
        }
}
