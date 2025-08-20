package com.pause.backend.models.requests

import java.time.LocalDateTime

data class CreateReviewRequest(
    val userId: Long,
    val question: String,
    val userAnswer: String,
    val correct: Boolean,
    val questionId: String? = null,
    val topic: String? = null,
    val clientEventId: String? = null,
    val timestamp: LocalDateTime = LocalDateTime.now()
)
