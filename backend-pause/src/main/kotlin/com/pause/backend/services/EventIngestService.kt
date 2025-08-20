package com.pause.backend.services

import com.pause.backend.exceptions.exceptions.DuplicateResourceException
import com.pause.backend.exceptions.exceptions.ResourceNotFoundException
import com.pause.backend.models.request.BatchEventsRequest
import com.pause.backend.models.responses.BatchEventsResponse
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class EventIngestService(
    private val pauseService: PauseService,
    private val reviewService: ReviewService,
    private val summaryService: SummaryService
) {
    @Transactional
    fun ingestBatch(request: BatchEventsRequest): BatchEventsResponse {
        var applied = 0
        var duplicates = 0

        request.events.forEach { ev ->
            when (ev.type) {
                "pause" -> {
                    val payload = ev.pause ?: throw ResourceNotFoundException("pause payload required")
                    try {
                        // Forzamos userId del batch para consistencia
                        pauseService.createPause(payload.copy(userId = request.userId))
                        applied++
                    } catch (_: DuplicateResourceException) {
                        duplicates++
                    }
                }
                "review" -> {
                    val payload = ev.review ?: throw ResourceNotFoundException("review payload required")
                    try {
                        reviewService.createReview(payload.copy(userId = request.userId))
                        applied++
                    } catch (_: DuplicateResourceException) {
                        duplicates++
                    }
                }
                else -> { /* ignora tipos desconocidos o agrega conteo de errores si quieres */ }
            }
        }

        val summary = summaryService.getSummary(request.userId)
        return BatchEventsResponse(
            appliedCount = applied,
            duplicates = duplicates,
            summary = summary
        )
    }
}