package com.googongill.aditory.exception.handler;

import com.googongill.aditory.common.ApiResponse;
import com.googongill.aditory.common.ErrorResponse;
import com.googongill.aditory.exception.BusinessException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.Errors;
import org.springframework.validation.FieldError;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingRequestHeaderException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.multipart.support.MissingServletRequestPartException;

import java.util.HashMap;
import java.util.Map;

import static com.googongill.aditory.common.code.CommonErrorCode.*;
import static com.googongill.aditory.common.code.ServerErrorCode.INDEX_OUT_OF_BOUND_ERROR;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    /**
     * 400 Bad Request
     */
    // Spring Validation -> BindException
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(MethodArgumentNotValidException.class)
    protected ResponseEntity<ApiResponse<Map<String, String>>> methodArgumentNotValidationException(MethodArgumentNotValidException e) {
        Errors errors = e.getBindingResult();
        Map<String, String> validateDetails = new HashMap<>();

        for (FieldError error : errors.getFieldErrors()) {
            String validKeyName = String.format("valid_error_of_%s", error.getField());
            validateDetails.put(validKeyName, error.getDefaultMessage());
        }
        return ApiResponse.fail(REQUEST_INVALID, validateDetails);
    }

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(HttpMessageNotReadableException.class)
    protected ResponseEntity<ErrorResponse> httpMessageNotReadableException(HttpMessageNotReadableException e) {
        ErrorResponse errorResponse = ErrorResponse.of(HTTP_REQUEST_INVALID);
        return ResponseEntity.status(errorResponse.getHttpStatus()).body(errorResponse);
    }

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(MissingRequestHeaderException.class)
    protected ResponseEntity<ErrorResponse> missingRequestHeaderException(MissingRequestHeaderException e) {
        ErrorResponse errorResponse = ErrorResponse.of(REQUEST_HEADER_MISSING);
        return ResponseEntity.status(errorResponse.getHttpStatus()).body(errorResponse);
    }

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(MissingServletRequestPartException.class)
    protected ResponseEntity<ErrorResponse> missingServletRequestPartException(MissingServletRequestPartException e) {
        ErrorResponse errorResponse = ErrorResponse.of(REQUEST_PART_MISSING);
        return ResponseEntity.status(errorResponse.getHttpStatus()).body(errorResponse);
    }

    /**
     * 405 Method Not Allowed
     */
    @ResponseStatus(HttpStatus.METHOD_NOT_ALLOWED)
    @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
    protected ResponseEntity<ErrorResponse> httpRequestMethodNotSupportedException(HttpRequestMethodNotSupportedException e) {
        ErrorResponse errorResponse = ErrorResponse.of(HTTP_METHOD_INVALID);
        return ResponseEntity.status(errorResponse.getHttpStatus()).body(errorResponse);
    }

    /**
     * 500 Internal Server Error
     */
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    @ExceptionHandler(IndexOutOfBoundsException.class)
    public ResponseEntity<ErrorResponse> indexOutOfBoundsException(IndexOutOfBoundsException e) {
        ErrorResponse errorResponse = ErrorResponse.of(INDEX_OUT_OF_BOUND_ERROR);
        return ResponseEntity.status(errorResponse.getHttpStatus()).body(errorResponse);
    }


    /**
     *  Business 예외
     */
    @ExceptionHandler(BusinessException.class)
    public ResponseEntity<ErrorResponse> businessException(BusinessException e) {
        log.error("BusinessException: {}", e.getMessage());
        return ResponseEntity.status(e.getHttpStatus())
                .body(ErrorResponse.of(e.getErrorCode()));
    }
}
