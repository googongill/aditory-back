package com.googongill.aditory.exception;

import com.googongill.aditory.common.code.BusinessErrorCode;

public class AWSS3Exception extends BusinessException {
    public AWSS3Exception(BusinessErrorCode errorCode) {
        super(errorCode);
    }
}
