package com.pause.backend.services

import com.pause.backend.exceptions.exceptions.DuplicateResourceException
import com.pause.backend.exceptions.exceptions.ResourceNotFoundException
import com.pause.backend.models.request.BatchEventsRequest
import com.pause.backend.models.requests.CreatePauseRequest
import com.pause.backend.models.requests.CreateReviewRequest
import com.pause.backend.models.responses.SummaryResponse
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.mockito.ArgumentCaptor
import org.mockito.Mockito.*
import java.time.LocalDateTime

class EventIngestServiceTest {

    private lateinit var pauseService: PauseService
    private lateinit var reviewService: ReviewService
    private lateinit var summaryService: SummaryService
    private lateinit var eventIngestService: EventIngestService

    @BeforeEach
    fun setUp() {
        pauseService = mock(PauseService::class.java)
        reviewService = mock(ReviewService::class.java)
        summaryService = mock(SummaryService::class.java)
        eventIngestService = EventIngestService(pauseService, reviewService, summaryService)
    }

    @Test
    fun `should apply pause and review and return summary`() {
        val userId = 1L
        val now = LocalDateTime.now()

        val pauseReq = CreatePauseRequest(userId = 999, durationMinutes = 5, timestamp = now)
        val reviewReq = CreateReviewRequest(userId = 888, question = "Q", userAnswer = "A", correct = true, timestamp = now)

        val batch = BatchEventsRequest(
            userId = userId,
            events = listOf(
                BatchEventsRequest.ClientEvent(type = "pause", pause = pauseReq),
                BatchEventsRequest.ClientEvent(type = "review", review = reviewReq),
                BatchEventsRequest.ClientEvent(type = "unknown")
            )
        )

        val summary = SummaryResponse(
            userId = userId,
            stateLevel = 3,
            score = 10,
            recentPausesCount = 1,
            recentReviewsCount = 1,
            unlockedItems = emptySet(),
            equippedItems = emptyMap(),
            nextUnlocks = emptyList(),
            lastUpdatedAt = now,
        )
        `when`(summaryService.getSummary(userId)).thenReturn(summary)

        val res = eventIngestService.ingestBatch(batch)

        assertEquals(2, res.appliedCount)
        assertEquals(0, res.duplicates)
        assertEquals(summary, res.summary)
        verify(summaryService, times(1)).getSummary(userId)

        // Captura y verifica que el userId se sobreescribió con el del batch
        val pauseCap = ArgumentCaptor.forClass(CreatePauseRequest::class.java)
        val reviewCap = ArgumentCaptor.forClass(CreateReviewRequest::class.java)
        verify(pauseService).createPause(pauseCap.capture())
        verify(reviewService).createReview(reviewCap.capture())
        assertEquals(userId, pauseCap.value.userId)
        assertEquals(5, pauseCap.value.durationMinutes)
        assertEquals(userId, reviewCap.value.userId)
        assertEquals("Q", reviewCap.value.question)
    }

    @Test
    fun `should count duplicates when underlying service throws DuplicateResourceException`() {
        val userId = 2L
        val now = LocalDateTime.now()
        val pauseReq = CreatePauseRequest(userId = 2L, durationMinutes = 3, timestamp = now)
        val reviewReq = CreateReviewRequest(userId = 2L, question = "Q", userAnswer = "A", correct = true, timestamp = now)

        val batch = BatchEventsRequest(
            userId = userId,
            events = listOf(
                BatchEventsRequest.ClientEvent(type = "pause", pause = pauseReq),
                BatchEventsRequest.ClientEvent(type = "review", review = reviewReq)
            )
        )

        doThrow(DuplicateResourceException("dup")).`when`(pauseService).createPause(any())
        `when`(summaryService.getSummary(userId)).thenReturn(
            SummaryResponse(userId, 3, 10, 1, 1, emptySet(), emptyMap(), emptyList(), now)
        )

        val res = eventIngestService.ingestBatch(batch)

        assertEquals(1, res.appliedCount) // solo review
        assertEquals(1, res.duplicates)   // pause duplicado
    }

    @Test
    fun should_throw_when_pause_payload_missing() {
        val batch = BatchEventsRequest(
            userId = 5L,
            events = listOf(BatchEventsRequest.ClientEvent(type = "pause", pause = null))
        )
        assertThrows<ResourceNotFoundException> { eventIngestService.ingestBatch(batch) }
    }
}