package com.pause.backend.models.requests

import java.time.LocalDateTime

data class CreatePauseRequest(
    val userId: Long,
    val durationMinutes: Int,
    val type: String? = null,
    val source: String? = null,
    val clientEventId: String? = null,
    val timestamp: LocalDateTime = LocalDateTime.now()
)
