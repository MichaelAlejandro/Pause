package com.pause.backend.models.request

import com.pause.backend.models.requests.CreatePauseRequest
import com.pause.backend.models.requests.CreateReviewRequest

data class BatchEventsRequest(
    val userId: Long,
    val events: List<ClientEvent>
) {
    data class ClientEvent(
        val type: String,
        val pause: CreatePauseRequest? = null,
        val review: CreateReviewRequest? = null
    )
}