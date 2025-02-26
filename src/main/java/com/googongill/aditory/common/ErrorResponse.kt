package com.googongill.aditory.common

import com.googongill.aditory.common.code.BusinessErrorCode
import org.springframework.http.HttpStatus

data class ErrorResponse(
    val httpStatus: HttpStatus,
    val message: String,
    val success: Boolean = false
) {

    companion object {
        fun of(businessErrorCode: BusinessErrorCode): ErrorResponse {
            return ErrorResponse(
                httpStatus = businessErrorCode.httpStatus,
                message = businessErrorCode.message
            )
        }
    }

}
