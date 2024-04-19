package com.googongill.aditory.exception;

import com.googongill.aditory.common.code.BusinessErrorCode;

public class UserException extends BusinessException {
    public UserException(BusinessErrorCode errorCode) {
        super(errorCode);
    }
}
