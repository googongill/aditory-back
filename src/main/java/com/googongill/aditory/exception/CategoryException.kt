package com.googongill.aditory.exception

import com.googongill.aditory.common.code.BusinessErrorCode

class CategoryException(errorCode: BusinessErrorCode?) : BusinessException(errorCode!!)
