package com.pause.backend.controllers

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.SerializationFeature
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule
import com.pause.backend.models.requests.CreateReviewRequest
import com.pause.backend.models.responses.ReviewResponse
import com.pause.backend.routes.Routes
import com.pause.backend.services.ReviewService
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
import org.springframework.test.web.client.match.MockRestRequestMatchers.jsonPath
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.delete
import org.springframework.test.web.servlet.get
import org.springframework.test.web.servlet.post
import java.time.LocalDateTime
import kotlin.test.assertEquals

@WebMvcTest(ReviewController::class)
@Import(ReviewControllerTest.MockConfig::class)
class ReviewControllerTest {

    @Autowired lateinit var mockMvc: MockMvc
    @Autowired lateinit var reviewService: ReviewService

    private lateinit var objectMapper: ObjectMapper
    private val BASE_URL = Routes.BASE_URL + Routes.REVIEWS

    @BeforeEach
    fun setup() {
        objectMapper = ObjectMapper()
            .registerModule(JavaTimeModule())
            .disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS)
    }

    @Test
    fun should_create_review() {
        val ts = LocalDateTime.of(2025, 7, 7, 20, 0)
        val request = CreateReviewRequest(
            userId = 1L,
            question = "¿Qué es Kotlin?",
            userAnswer = "Un lenguaje de programación",
            correct = true,
            questionId = "q1",
            topic = "kotlin",
            clientEventId = "evt-456",
            timestamp = ts
        )
        val response = ReviewResponse(
            id = 1L,
            userId = 1L,
            question = "¿Qué es Kotlin?",
            userAnswer = "Un lenguaje de programación",
            correct = true,
            questionId = "q1",
            topic = "kotlin",
            clientEventId = "evt-456",
            timestamp = ts
        )

        `when`(reviewService.createReview(request)).thenReturn(response)

        val result = mockMvc.post(BASE_URL) {
            contentType = MediaType.APPLICATION_JSON
            content = objectMapper.writeValueAsString(request)
        }.andExpect {
            status { isCreated() }
            jsonPath("$.id") { value(1) }
            jsonPath("$.userId") { value(1) }
            jsonPath("$.questionId") { value("q1") }
            jsonPath("$.topic") { value("kotlin") }
            jsonPath("$.clientEventId") { value("evt-456") }
            jsonPath("$.correct") { value(true) }
        }.andReturn()

        assertEquals(201, result.response.status)
    }

    @Test
    fun should_get_all_reviews() {
        val response = listOf(
            ReviewResponse(1L, 1L, "¿Qué es Kotlin?", "Lenguaje", true, "q1", "kotlin", "evt-1", LocalDateTime.now())
        )
        `when`(reviewService.getAll()).thenReturn(response)

        val result = mockMvc.get(BASE_URL)
            .andExpect {
                status { isOk() }
                jsonPath("$.size()") { value(1) }
                jsonPath("$[0].userId") { value(1) }
                jsonPath("$[0].questionId") { value("q1") }
            }.andReturn()

        assertEquals(200, result.response.status)
    }

    @Test
    fun should_get_review_by_id() {
        val response = ReviewResponse(1L, 1L, "¿Qué es Kotlin?", "Lenguaje", false, null, null, null, LocalDateTime.now())
        `when`(reviewService.getById(1L)).thenReturn(response)

        val result = mockMvc.get("$BASE_URL/1")
            .andExpect {
                status { isOk() }
                jsonPath("$.question") { value("¿Qué es Kotlin?") }
                jsonPath("$.correct") { value(false) }
                jsonPath("$.questionId").doesNotExist()
            }.andReturn()

        assertEquals(200, result.response.status)
    }

    @Test
    fun should_delete_review() {
        val result = mockMvc.delete("$BASE_URL/1")
            .andExpect { status { isNoContent() } }
            .andReturn()
        assertEquals(204, result.response.status)
    }

    @TestConfiguration
    class MockConfig {
        @Bean fun reviewService(): ReviewService = mock(ReviewService::class.java)
    }
}