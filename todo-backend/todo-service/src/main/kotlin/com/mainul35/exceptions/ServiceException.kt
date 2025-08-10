package com.mainul35.exceptions

import java.lang.RuntimeException

class ServiceException(message: String, cause: Throwable? = null) : RuntimeException(message, cause)