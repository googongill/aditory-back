package com.googongill.aditory.exception.handler

import com.googongill.aditory.common.ApiResponse
import com.googongill.aditory.common.ErrorResponse
import com.googongill.aditory.common.code.CommonErrorCode
import com.googongill.aditory.common.code.ServerErrorCode
import com.googongill.aditory.exception.BusinessException
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.http.converter.HttpMessageNotReadableException
import org.springframework.validation.Errors
import org.springframework.web.HttpRequestMethodNotSupportedException
import org.springframework.web.bind.MethodArgumentNotValidException
import org.springframework.web.bind.MissingRequestHeaderException
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.ResponseStatus
import org.springframework.web.bind.annotation.RestControllerAdvice
import org.springframework.web.multipart.support.MissingServletRequestPartException

@RestControllerAdvice
class GlobalExceptionHandler {

    /**
     * 400 Bad Request
     */
    // Spring Validation -> BindException
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(MethodArgumentNotValidException::class)
    protected fun methodArgumentNotValidationException(e: MethodArgumentNotValidException): ResponseEntity<ApiResponse<Map<String, String>>> {
        val errors: Errors = e.bindingResult
        val validateDetails: MutableMap<String, String> = HashMap()
        for (error in errors.fieldErrors) {
            val validKeyName = String.format("valid_error_of_%s", error.field)
            validateDetails[validKeyName] = error.defaultMessage
        }
        return ApiResponse.fail(CommonErrorCode.REQUEST_INVALID, validateDetails)
    }

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(HttpMessageNotReadableException::class)
    protected fun httpMessageNotReadableException(e: HttpMessageNotReadableException?): ResponseEntity<ErrorResponse> {
        val errorResponse = ErrorResponse.of(CommonErrorCode.HTTP_REQUEST_INVALID)
        return ResponseEntity.status(errorResponse.httpStatus).body(errorResponse)
    }

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(MissingRequestHeaderException::class)
    protected fun missingRequestHeaderException(e: MissingRequestHeaderException?): ResponseEntity<ErrorResponse> {
        val errorResponse = ErrorResponse.of(CommonErrorCode.REQUEST_HEADER_MISSING)
        return ResponseEntity.status(errorResponse.httpStatus).body(errorResponse)
    }

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(MissingServletRequestPartException::class)
    protected fun missingServletRequestPartException(e: MissingServletRequestPartException?): ResponseEntity<ErrorResponse> {
        val errorResponse = ErrorResponse.of(CommonErrorCode.REQUEST_PART_MISSING)
        return ResponseEntity.status(errorResponse.httpStatus).body(errorResponse)
    }

    /**
     * 405 Method Not Allowed
     */
    @ResponseStatus(HttpStatus.METHOD_NOT_ALLOWED)
    @ExceptionHandler(HttpRequestMethodNotSupportedException::class)
    protected fun httpRequestMethodNotSupportedException(e: HttpRequestMethodNotSupportedException?): ResponseEntity<ErrorResponse> {
        val errorResponse = ErrorResponse.of(CommonErrorCode.HTTP_METHOD_INVALID)
        return ResponseEntity.status(errorResponse.httpStatus).body(errorResponse)
    }

    /**
     * 500 Internal Server Error
     */
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    @ExceptionHandler(IndexOutOfBoundsException::class)
    fun indexOutOfBoundsException(e: IndexOutOfBoundsException?): ResponseEntity<ErrorResponse> {
        val errorResponse = ErrorResponse.of(ServerErrorCode.INDEX_OUT_OF_BOUND_ERROR)
        return ResponseEntity.status(errorResponse.httpStatus).body(errorResponse)
    }

    /**
     * Business 예외
     */
    @ExceptionHandler(BusinessException::class)
    fun businessException(e: BusinessException): ResponseEntity<ErrorResponse> {
        return ResponseEntity.status(e.getHttpStatus())
            .body(ErrorResponse.of(e.errorCode))
    }

}
