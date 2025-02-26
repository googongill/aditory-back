package com.googongill.aditory.common

import com.fasterxml.jackson.annotation.JsonInclude
import com.fasterxml.jackson.annotation.JsonPropertyOrder
import com.googongill.aditory.common.code.BusinessErrorCode
import com.googongill.aditory.common.code.SuccessCode
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity

@JsonPropertyOrder("httpStatus", "message", "success", "data")
data class ApiResponse<T>(
    val httpStatus: HttpStatus,
    val message: String,
    val success: Boolean,

    @JsonInclude(JsonInclude.Include.NON_NULL)
    val data: T? = null
) {

    companion object {
        // 성공
        fun <T> success(successCode: SuccessCode): ResponseEntity<ApiResponse<T>> =
            ResponseEntity(
                ApiResponse(
                    httpStatus = successCode.httpStatus,
                    message = successCode.message,
                    success = true
                ),
                successCode.httpStatus
            )

        fun <T> success(successCode: SuccessCode, data: T): ResponseEntity<ApiResponse<T>> =
            ResponseEntity(
                ApiResponse(
                    httpStatus = successCode.httpStatus,
                    message = successCode.message,
                    success = true,
                    data = data
                ),
                successCode.httpStatus
            )

        // 실패
        fun <T> fail(businessErrorCode: BusinessErrorCode): ResponseEntity<ApiResponse<T>> =
            ResponseEntity(
                ApiResponse(
                    httpStatus = businessErrorCode.httpStatus,
                    message = businessErrorCode.message,
                    success = false
                ),
                businessErrorCode.httpStatus
            )

        fun <T> fail(businessErrorCode: BusinessErrorCode, data: T): ResponseEntity<ApiResponse<T>> =
            ResponseEntity(
                ApiResponse(
                    httpStatus = businessErrorCode.httpStatus,
                    message = businessErrorCode.message,
                    success = false,
                    data = data
                ),
                businessErrorCode.httpStatus
            )
    }
}
