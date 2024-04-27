package com.googongill.aditory.exception;

import com.googongill.aditory.common.code.BusinessErrorCode;

public class CategoryException extends BusinessException {
    public CategoryException(BusinessErrorCode errorCode) {
        super(errorCode);
    }
}
