package com.pause.frontend.data.remote.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class BatchEventsRequest(
    @SerialName("userId") val userId: Long,
    @SerialName("events") val events: List<EventEnvelope>
)

@Serializable
data class EventEnvelope(
    // "pause" | "review"
    @SerialName("type") val type: String,
    @SerialName("pause") val pause: CreatePauseRequest? = null,
    @SerialName("review") val review: CreateReviewRequest? = null
)

@Serializable
data class BatchEventsResponse(
    @SerialName("appliedCount") val appliedCount: Int,
    @SerialName("duplicates") val duplicates: Int,
    // servidor devuelve summary actualizado tras aplicar el batch
    @SerialName("summary") val summary: SummaryResponse? = null
)