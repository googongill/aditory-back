package com.googongill.aditory.exception

import com.googongill.aditory.common.code.BusinessErrorCode

class UserException(errorCode: BusinessErrorCode?) : BusinessException(errorCode!!)
