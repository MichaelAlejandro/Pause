package com.pause.backend.models.responses

data class ErrorResponse(
    val code: String,
    val message: String,
    val details: Map<String, String>? = null
)