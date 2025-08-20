package com.pause.backend.models.responses

data class BatchEventsResponse(
    val appliedCount: Int,
    val duplicates: Int,
    val summary: SummaryResponse
)