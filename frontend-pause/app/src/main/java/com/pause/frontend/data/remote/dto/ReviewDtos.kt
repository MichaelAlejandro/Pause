package com.pause.frontend.data.remote.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class CreateReviewRequest(
    @SerialName("userId") val userId: Long,
    @SerialName("question") val question: String,
    @SerialName("userAnswer") val userAnswer: String,
    @SerialName("correct") val correct: Boolean,
    @SerialName("questionId") val questionId: String,
    @SerialName("topic") val topic: String,
    @SerialName("clientEventId") val clientEventId: String,
    @SerialName("timestamp") val timestamp: String
)

@Serializable
data class ReviewResponse(
    @SerialName("id") val id: Long,
    @SerialName("userId") val userId: Long,
    @SerialName("question") val question: String,
    @SerialName("userAnswer") val userAnswer: String,
    @SerialName("correct") val correct: Boolean,
    @SerialName("questionId") val questionId: String? = null,
    @SerialName("topic") val topic: String? = null,
    @SerialName("clientEventId") val clientEventId: String? = null,
    @SerialName("timestamp") val timestamp: String? = null
)