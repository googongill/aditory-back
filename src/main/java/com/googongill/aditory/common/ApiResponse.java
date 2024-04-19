package com.googongill.aditory.common;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.googongill.aditory.common.code.BusinessErrorCode;
import com.googongill.aditory.common.code.SuccessCode;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

@Getter
@Builder
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@JsonPropertyOrder({"httpStatus", "message", "success", "data"})
public class ApiResponse<T> {

    private final HttpStatus httpStatus;
    private final String message;
    private final boolean success;

    @JsonInclude(JsonInclude.Include.NON_NULL)
    private T data;

    // 성공
    public static <T> ResponseEntity<ApiResponse<T>> success(SuccessCode successCode) {
        return ResponseEntity.status(successCode.getHttpStatus())
                .body(ApiResponse.<T>builder()
                        .httpStatus(successCode.getHttpStatus())
                        .message(successCode.getMessage())
                        .success(true)
                        .build());
    }

    public static <T> ResponseEntity<ApiResponse<T>> success(SuccessCode successCode, T data) {
        return ResponseEntity.status(successCode.getHttpStatus())
                .body(ApiResponse.<T>builder()
                        .httpStatus(successCode.getHttpStatus())
                        .message(successCode.getMessage())
                        .success(true)
                        .data(data)
                        .build());
    }

    // 실패
    public static <T> ResponseEntity<ApiResponse<T>> fail(BusinessErrorCode businessErrorCode) {
        return ResponseEntity.status(businessErrorCode.getHttpStatus())
                .body(ApiResponse.<T>builder()
                        .httpStatus(businessErrorCode.getHttpStatus())
                        .message(businessErrorCode.getMessage())
                        .success(false)
                        .build());
    }

    public static <T> ResponseEntity<ApiResponse<T>> fail(BusinessErrorCode businessErrorCode, T data) {
        return ResponseEntity.status(businessErrorCode.getHttpStatus())
                .body(ApiResponse.<T>builder()
                        .httpStatus(businessErrorCode.getHttpStatus())
                        .message(businessErrorCode.getMessage())
                        .success(false)
                        .data(data)
                        .build());
    }
}
