package com.pause.frontend.data.remote.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class CreatePauseRequest(
    @SerialName("userId") val userId: Long,
    @SerialName("durationMinutes") val durationMinutes: Int,
    @SerialName("type") val type: String = "active",
    @SerialName("source") val source: String = "manual",
    @SerialName("clientEventId") val clientEventId: String,
    // ISO-8601: 2025-08-18T12:00:00
    @SerialName("timestamp") val timestamp: String
)

@Serializable
data class PauseResponse(
    @SerialName("id") val id: Long,
    @SerialName("userId") val userId: Long,
    @SerialName("durationMinutes") val durationMinutes: Int,
    @SerialName("type") val type: String? = null,
    @SerialName("source") val source: String? = null,
    @SerialName("clientEventId") val clientEventId: String? = null,
    @SerialName("timestamp") val timestamp: String? = null
)