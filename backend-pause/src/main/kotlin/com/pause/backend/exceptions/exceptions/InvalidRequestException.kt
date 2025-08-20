package com.pause.backend.exceptions.exceptions

class InvalidRequestException(
    message: String,
    val details: Map<String, String>? = null
) : RuntimeException(message)
