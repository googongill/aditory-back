package com.googongill.aditory.exception;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import static com.googongill.aditory.common.code.CommonErrorCode.BIND_EXCEPTION;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    /**
     * Spring Bean Validation 관련 예외
     * - 추후 구현 필요
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> bindException(MethodArgumentNotValidException e) {
        log.error("BindException: {}", e.getMessage());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(ErrorResponse.error(BIND_EXCEPTION));
    }

    /**
     *  Business 예외
     */
    @ExceptionHandler(BusinessException.class)
    public ResponseEntity<ErrorResponse> businessException(BusinessException e) {
        log.error("BusinessException: {}", e.getMessage());
        return ResponseEntity.status(e.getHttpStatus())
                .body(ErrorResponse.error(e.getErrorCode()));
    }
}
