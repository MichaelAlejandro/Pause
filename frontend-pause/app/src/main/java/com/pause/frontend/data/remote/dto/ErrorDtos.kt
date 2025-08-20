package com.pause.frontend.data.remote.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class ErrorResponse(
    // p.ej. VALIDATION_ERROR, NOT_FOUND, DUPLICATE_RESOURCE
    @SerialName("code") val code: String,
    @SerialName("message") val message: String
)