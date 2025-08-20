package com.pause.backend.models.responses

import java.time.LocalDateTime

data class ReviewResponse(
    val id: Long,
    val userId: Long,
    val question: String,
    val userAnswer: String,
    val correct: Boolean,
    val questionId: String?,
    val topic: String?,
    val clientEventId: String?,
    val timestamp: LocalDateTime
)
