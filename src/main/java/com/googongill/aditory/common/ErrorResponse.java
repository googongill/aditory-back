package com.googongill.aditory.common;

import com.googongill.aditory.common.code.BusinessErrorCode;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@Builder
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class ErrorResponse {

    private final HttpStatus httpStatus;
    private final String message;
    private final boolean success;


    public static ErrorResponse of(BusinessErrorCode businessErrorCode) {
        return ErrorResponse.builder()
                .httpStatus(businessErrorCode.getHttpStatus())
                .message(businessErrorCode.getMessage())
                .success(false)
                .build();
    }
}
