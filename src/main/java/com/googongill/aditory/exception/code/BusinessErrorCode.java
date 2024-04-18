package com.googongill.aditory.exception.code;

import org.springframework.http.HttpStatus;

public interface BusinessErrorCode {
    HttpStatus getHttpStatus();
    String getMessage();
}
