package com.googongill.aditory.exception

import com.googongill.aditory.common.code.BusinessErrorCode
import org.springframework.http.HttpStatus

open class BusinessException : RuntimeException {

    val errorCode: BusinessErrorCode

    constructor(errorCode: BusinessErrorCode) : super(errorCode.message) {
        this.errorCode = errorCode
    }

    constructor(message: String, errorCode: BusinessErrorCode) : super(message) {
        this.errorCode = errorCode
    }

    fun getHttpStatus(): HttpStatus = errorCode.httpStatus

}
