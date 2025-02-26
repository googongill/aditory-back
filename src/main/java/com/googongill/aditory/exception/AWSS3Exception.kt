package com.googongill.aditory.exception

import com.googongill.aditory.common.code.BusinessErrorCode

class AWSS3Exception(errorCode: BusinessErrorCode?) : BusinessException(errorCode!!)
