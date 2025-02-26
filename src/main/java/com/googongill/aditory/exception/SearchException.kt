package com.googongill.aditory.exception

import com.googongill.aditory.common.code.BusinessErrorCode

class SearchException(errorCode: BusinessErrorCode?) : BusinessException(errorCode!!)
