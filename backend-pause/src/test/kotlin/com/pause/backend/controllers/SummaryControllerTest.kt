package com.pause.backend.controllers

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.SerializationFeature
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule
import com.pause.backend.models.responses.SummaryResponse
import com.pause.backend.models.responses.UnlockPreview
import com.pause.backend.routes.Routes
import com.pause.backend.services.SummaryService
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.mockito.Mockito.`when`
import org.mockito.Mockito.mock
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest
import org.springframework.boot.test.context.TestConfiguration
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Import
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.get
import java.time.LocalDateTime
import kotlin.test.assertEquals

@WebMvcTest(SummaryController::class)
@Import(SummaryControllerTest.MockConfig::class)
class SummaryControllerTest {

    @Autowired lateinit var mockMvc: MockMvc
    @Autowired lateinit var summaryService: SummaryService

    private lateinit var objectMapper: ObjectMapper
    private val BASE_URL = Routes.BASE_URL + Routes.SUMMARY

    @BeforeEach
    fun setup() {
        objectMapper = ObjectMapper()
            .registerModule(JavaTimeModule())
            .disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS)
    }

    @Test
    fun should_get_summary_by_user() {
        val resp = SummaryResponse(
            userId = 1L,
            stateLevel = 4,
            score = 60,
            recentPausesCount = 3,
            recentReviewsCount = 5,
            unlockedItems = setOf("hat_red", "sunglasses"),
            equippedItems = mapOf("hat" to "hat_red"),
            nextUnlocks = listOf(UnlockPreview("crown", "2 repasos más", 1, 3)),
            lastUpdatedAt = LocalDateTime.now(),
            userName = "michael",
            petName = "nano"
        )

        `when`(summaryService.getSummary(1L)).thenReturn(resp)

        val result = mockMvc.get("$BASE_URL?userId=1")
            .andExpect {
                status { isOk() }
                jsonPath("$.userId") { value(1) }
                jsonPath("$.stateLevel") { value(4) }
                jsonPath("$.recentPausesCount") { value(3) }
                jsonPath("$.nextUnlocks.length()") { value(1) }
                jsonPath("$.equippedItems.hat") { value("hat_red") }
            }.andReturn()

        assertEquals(200, result.response.status)
    }

    @TestConfiguration
    class MockConfig {
        @Bean fun summaryService(): SummaryService = mock(SummaryService::class.java)
    }
}