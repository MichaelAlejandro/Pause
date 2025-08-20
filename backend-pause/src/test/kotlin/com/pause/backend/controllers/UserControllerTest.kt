package com.pause.backend.controllers

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.SerializationFeature
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule
import com.pause.backend.models.requests.CreateUserRequest
import com.pause.backend.models.responses.*
import com.pause.backend.routes.Routes
import com.pause.backend.services.UserService
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
import org.springframework.test.web.servlet.put
import java.time.LocalDateTime
import kotlin.test.assertEquals

@WebMvcTest(UserController::class)
@Import(UserControllerTest.MockConfig::class)
class UserControllerTest {

    @Autowired lateinit var mockMvc: MockMvc
    @Autowired lateinit var userService: UserService

    private lateinit var objectMapper: ObjectMapper
    private val BASE_URL = Routes.BASE_URL + Routes.USERS

    @BeforeEach
    fun setup() {
        objectMapper = ObjectMapper()
            .registerModule(JavaTimeModule())
            .disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS)
    }

    @Test
    fun should_create_user() {
        val request = CreateUserRequest("uid123", "Michael", "maalejandro@puce.edu.ec")
        val response = UserResponse(1L, request.uid, request.userName, request.email)

        `when`(userService.createUser(request)).thenReturn(response)

        val result = mockMvc.post(BASE_URL) {
            contentType = MediaType.APPLICATION_JSON
            content = objectMapper.writeValueAsString(request)
        }.andExpect {
            status { isCreated() }
            jsonPath("$.id") { value(1) }
            jsonPath("$.uid") { value("uid123") }
            jsonPath("$.userName") { value("Michael") }
            jsonPath("$.email") { value("maalejandro@puce.edu.ec") }
        }.andReturn()

        assertEquals(201, result.response.status)
    }

    @Test
    fun should_get_all_users() {
        val response = listOf(UserResponse(1L, "uid123", "Michael", "maalejandro@puce.edu.ec"))
        `when`(userService.getAllUsers()).thenReturn(response)

        val result = mockMvc.get(BASE_URL)
            .andExpect {
                status { isOk() }
                jsonPath("$.size()") { value(1) }
                jsonPath("$[0].userName") { value("Michael") }
            }.andReturn()

        assertEquals(200, result.response.status)
    }

    @Test
    fun should_get_user_by_id() {
        val response = UserResponse(1L, "uid123", "Michael", "maalejandro@puce.edu.ec")
        `when`(userService.getUserById(1L)).thenReturn(response)

        val result = mockMvc.get("$BASE_URL/1")
            .andExpect {
                status { isOk() }
                jsonPath("$.id") { value(1) }
                jsonPath("$.userName") { value("Michael") }
            }.andReturn()

        assertEquals(200, result.response.status)
    }

    @Test
    fun should_update_user() {
        val request = CreateUserRequest("uid123", "Michael A.", "nuevo@puce.edu.ec")
        val response = UserResponse(1L, request.uid, request.userName, request.email)
        `when`(userService.updateUser(1L, request)).thenReturn(response)

        val result = mockMvc.put("$BASE_URL/1") {
            contentType = MediaType.APPLICATION_JSON
            content = objectMapper.writeValueAsString(request)
        }.andExpect {
            status { isOk() }
            jsonPath("$.userName") { value("Michael A.") }
            jsonPath("$.email") { value("nuevo@puce.edu.ec") }
        }.andReturn()

        assertEquals(200, result.response.status)
    }

    @Test
    fun should_delete_user() {
        val result = mockMvc.delete("$BASE_URL/1")
            .andExpect { status { isNoContent() } }
            .andReturn()
        assertEquals(204, result.response.status)
    }

    @Test
    fun should_get_user_details() {
        val user = UserResponse(1L, "uid123", "Michael", "maalejandro@puce.edu.ec")
        val pet = PetResponse(
            id = 10L, userId = 1L, petName = "Neo", stateLevel = 4, score = 50,
            unlockedItems = setOf("hat_red"), equippedItems = mapOf("hat" to "hat_red"),
            customization = "azul", lastUpdatedAt = LocalDateTime.now()
        )
        val pauses = listOf(PauseResponse(5L, 1L, 10, "active", "android", "evt-1", LocalDateTime.now()))
        val reviews = listOf(ReviewResponse(7L, 1L, "Q", "A", true, "q1", "topic", "evt-2", LocalDateTime.now()))
        val details = UserDetailResponse(user, pet, pauses, reviews)

        `when`(userService.getUserDetails(1L)).thenReturn(details)

        val result = mockMvc.get("$BASE_URL/1/details")
            .andExpect {
                status { isOk() }
                jsonPath("$.user.id") { value(1) }
                jsonPath("$.pet.petName") { value("Neo") }
                jsonPath("$.pauses.length()") { value(1) }
                jsonPath("$.reviews.length()") { value(1) }
            }.andReturn()

        assertEquals(200, result.response.status)
    }

    @TestConfiguration
    class MockConfig {
        @Bean fun userService(): UserService = mock(UserService::class.java)
    }
}