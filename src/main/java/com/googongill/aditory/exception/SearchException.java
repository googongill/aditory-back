package com.googongill.aditory.exception;

import com.googongill.aditory.common.code.BusinessErrorCode;

public class SearchException extends BusinessException {
    public SearchException(BusinessErrorCode errorCode) {
        super(errorCode);
    }

}
