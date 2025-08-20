package com.pause.backend.controllers

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.SerializationFeature
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule
import com.pause.backend.models.requests.CreatePauseRequest
import com.pause.backend.models.responses.PauseResponse
import com.pause.backend.routes.Routes
import com.pause.backend.services.PauseService
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
import org.springframework.test.web.servlet.delete
import org.springframework.test.web.servlet.get
import org.springframework.test.web.servlet.post
import java.time.LocalDateTime
import kotlin.test.assertEquals

@WebMvcTest(PauseController::class)
@Import(PauseControllerTest.MockConfig::class)
class PauseControllerTest {

    @Autowired lateinit var mockMvc: MockMvc
    @Autowired lateinit var pauseService: PauseService

    private lateinit var objectMapper: ObjectMapper
    private val BASE_URL = Routes.BASE_URL + Routes.PAUSES

    @BeforeEach
    fun setup() {
        objectMapper = ObjectMapper()
            .registerModule(JavaTimeModule())
            .disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS)
    }

    @Test
    fun should_create_pause() {
        val ts = LocalDateTime.of(2025, 7, 7, 21, 0)
        val request = CreatePauseRequest(
            userId = 1L,
            durationMinutes = 5,
            type = "active",
            source = "android",
            clientEventId = "evt-123",
            timestamp = ts
        )

        val response = PauseResponse(
            id = 1L,
            userId = 1L,
            durationMinutes = 5,
            type = "active",
            source = "android",
            clientEventId = "evt-123",
            timestamp = ts
        )

        `when`(pauseService.createPause(request)).thenReturn(response)

        val result = mockMvc.post(BASE_URL) {
            contentType = MediaType.APPLICATION_JSON
            content = objectMapper.writeValueAsString(request)
        }.andExpect {
            status { isCreated() }
            jsonPath("$.id") { value(1) }
            jsonPath("$.userId") { value(1) }
            jsonPath("$.durationMinutes") { value(5) }
            jsonPath("$.type") { value("active") }
            jsonPath("$.source") { value("android") }
            jsonPath("$.clientEventId") { value("evt-123") }
        }.andReturn()

        assertEquals(201, result.response.status)
    }

    @Test
    fun should_get_all_pauses() {
        val response = listOf(
            PauseResponse(1L, 1L, 10, "active", "android", "evt-1", LocalDateTime.now())
        )
        `when`(pauseService.getAll()).thenReturn(response)

        val result = mockMvc.get(BASE_URL)
            .andExpect {
                status { isOk() }
                jsonPath("$.size()") { value(1) }
                jsonPath("$[0].userId") { value(1) }
                jsonPath("$[0].durationMinutes") { value(10) }
            }.andReturn()

        assertEquals(200, result.response.status)
    }

    @Test
    fun should_get_pause_by_id() {
        val resp = PauseResponse(1L, 1L, 15, "active", "android", "evt-9", LocalDateTime.now())
        `when`(pauseService.getById(1L)).thenReturn(resp)

        val result = mockMvc.get("$BASE_URL/1")
            .andExpect {
                status { isOk() }
                jsonPath("$.durationMinutes") { value(15) }
                jsonPath("$.userId") { value(1) }
                jsonPath("$.clientEventId") { value("evt-9") }
            }.andReturn()

        assertEquals(200, result.response.status)
    }

    @Test
    fun should_delete_pause() {
        val result = mockMvc.delete("$BASE_URL/1")
            .andExpect { status { isNoContent() } }
            .andReturn()
        assertEquals(204, result.response.status)
    }

    @TestConfiguration
    class MockConfig {
        @Bean fun pauseService(): PauseService = mock(PauseService::class.java)
    }
}