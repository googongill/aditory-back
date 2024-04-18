package com.googongill.aditory.exception;

import com.googongill.aditory.exception.code.BusinessErrorCode;

public class UserException extends BusinessException {

    public UserException(BusinessErrorCode errorCode) {
        super(errorCode);
    }
}
