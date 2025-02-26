package com.googongill.aditory.common.code

import org.springframework.http.HttpStatus

interface BusinessErrorCode {
    val httpStatus: HttpStatus
    val message: String
}