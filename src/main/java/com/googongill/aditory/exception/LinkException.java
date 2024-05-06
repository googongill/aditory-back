package com.googongill.aditory.exception;

import com.googongill.aditory.common.code.BusinessErrorCode;

public class LinkException extends BusinessException {
    public LinkException(BusinessErrorCode errorCode) {
        super(errorCode);
    }
}
