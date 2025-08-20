package com.pause.backend.models.responses

import java.time.LocalDateTime

data class PauseResponse(
    val id: Long,
    val userId: Long,
    val durationMinutes: Int,
    val type: String?,
    val source: String?,
    val clientEventId: String?,
    val timestamp: LocalDateTime
)
