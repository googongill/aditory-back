package com.googongill.aditory.common.code;

import org.springframework.http.HttpStatus;

public interface BusinessErrorCode {
    HttpStatus getHttpStatus();
    String getMessage();
}
