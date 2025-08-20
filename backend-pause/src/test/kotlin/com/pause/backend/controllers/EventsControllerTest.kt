package com.pause.backend.controllers

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.SerializationFeature
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule
import com.pause.backend.models.request.BatchEventsRequest
import com.pause.backend.models.requests.CreatePauseRequest
import com.pause.backend.models.requests.CreateReviewRequest
import com.pause.backend.models.responses.*
import com.pause.backend.routes.Routes
import com.pause.backend.services.EventIngestService
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.mockito.Mockito.`when`
import org.mockito.Mockito.mock
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest
import org.springframework.boot.test.context.TestConfiguration
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Import
import org.springframework.http.MediaType
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.post
import java.time.LocalDateTime
import kotlin.test.assertEquals

@WebMvcTest(EventsController::class)
@Import(EventsControllerTest.MockConfig::class)
class EventsControllerTest {

    @Autowired lateinit var mockMvc: MockMvc
    @Autowired lateinit var eventIngestService: EventIngestService

    private lateinit var objectMapper: ObjectMapper
    private val BASE_URL = Routes.BASE_URL + Routes.EVENTS + Routes.BATCH

    @BeforeEach
    fun setup() {
        objectMapper = ObjectMapper()
            .registerModule(JavaTimeModule())
            .disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS)
    }

    @Test
    fun should_ingest_batch_events_and_return_accepted() {
        val now = LocalDateTime.of(2025, 7, 8, 10, 0)
        val req = BatchEventsRequest(
            userId = 1L,
            events = listOf(
                BatchEventsRequest.ClientEvent(
                    type = "pause",
                    pause = CreatePauseRequest(1L, 5, "active", "android", "evt-1", now)
                ),
                BatchEventsRequest.ClientEvent(
                    type = "review",
                    review = CreateReviewRequest(1L, "Q", "A", true, "q1", "kotlin", "evt-2", now)
                )
            )
        )

        val summary = SummaryResponse(
            userId = 1L,
            stateLevel = 5,
            score = 70,
            recentPausesCount = 1,
            recentReviewsCount = 1,
            unlockedItems = setOf("hat_red"),
            equippedItems = mapOf("hat" to "hat_red"),
            nextUnlocks = emptyList(),
            lastUpdatedAt = now,
            userName = "michael",
            petName = "nano"
        )

        val resp = BatchEventsResponse(appliedCount = 2, duplicates = 0, summary = summary)

        `when`(eventIngestService.ingestBatch(req)).thenReturn(resp)

        val result = mockMvc.post(BASE_URL) {
            contentType = MediaType.APPLICATION_JSON
            content = objectMapper.writeValueAsString(req)
        }.andExpect {
            status { isAccepted() }
            jsonPath("$.appliedCount") { value(2) }
            jsonPath("$.duplicates") { value(0) }
            jsonPath("$.summary.userId") { value(1) }
            jsonPath("$.summary.stateLevel") { value(5) }
        }.andReturn()

        assertEquals(202, result.response.status)
    }

    @TestConfiguration
    class MockConfig {
        @Bean fun eventIngestService(): EventIngestService = mock(EventIngestService::class.java)
    }
}