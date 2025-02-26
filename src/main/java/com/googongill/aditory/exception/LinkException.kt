package com.googongill.aditory.exception

import com.googongill.aditory.common.code.BusinessErrorCode

class LinkException(errorCode: BusinessErrorCode?) : BusinessException(errorCode!!)
