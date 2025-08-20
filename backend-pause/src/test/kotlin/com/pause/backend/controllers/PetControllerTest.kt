package com.pause.backend.controllers

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.SerializationFeature
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule
import com.pause.backend.models.request.UpdatePetCustomizationRequest
import com.pause.backend.models.request.UpdatePetEquipRequest
import com.pause.backend.models.request.UpdatePetNameRequest
import com.pause.backend.models.requests.CreatePetRequest
import com.pause.backend.models.responses.PetResponse
import com.pause.backend.routes.Routes
import com.pause.backend.services.PetService
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
import org.springframework.test.web.servlet.patch
import org.springframework.test.web.servlet.post
import org.springframework.test.web.servlet.put
import java.time.LocalDateTime
import kotlin.test.assertEquals
@WebMvcTest(PetController::class)
@Import(PetControllerTest.MockConfig::class)
class PetControllerTest {

    @Autowired lateinit var mockMvc: MockMvc
    @Autowired lateinit var petService: PetService

    private lateinit var objectMapper: ObjectMapper
    private val BASE_URL = Routes.BASE_URL + Routes.PETS

    @BeforeEach
    fun setup() {
        objectMapper = ObjectMapper()
            .registerModule(JavaTimeModule())
            .disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS)
    }

    private fun samplePetResponse(
        id: Long = 1L,
        userId: Long = 1L,
        petName: String = "Neo",
        stateLevel: Int = 4,
        score: Int = 35,
        unlocked: Set<String> = setOf("hat", "glasses"),
        equipped: Map<String, String> = mapOf("hat" to "hat_red"),
        customization: String? = "azul",
        updatedAt: LocalDateTime = LocalDateTime.of(2025, 7, 7, 22, 0)
    ) = PetResponse(
        id = id,
        userId = userId,
        petName = petName,
        stateLevel = stateLevel,
        score = score,
        unlockedItems = unlocked,
        equippedItems = equipped,
        customization = customization,
        lastUpdatedAt = updatedAt
    )

    @Test
    fun should_create_pet() {
        val request = CreatePetRequest(
            userId = 1L,
            petName = "Neo",
            customization = "azul"
        )

        val response = samplePetResponse()

        `when`(petService.createPet(request)).thenReturn(response)

        val result = mockMvc.post(BASE_URL) {
            contentType = MediaType.APPLICATION_JSON
            content = objectMapper.writeValueAsString(request)
        }.andExpect {
            status { isCreated() }
            jsonPath("$.id") { value(1) }
            jsonPath("$.userId") { value(1) }
            jsonPath("$.petName") { value("Neo") }
            jsonPath("$.stateLevel") { value(4) }
            jsonPath("$.score") { value(35) }
            jsonPath("$.unlockedItems.length()") { value(2) }
            jsonPath("$.equippedItems.hat") { value("hat_red") }
            jsonPath("$.customization") { value("azul") }
        }.andReturn()

        assertEquals(201, result.response.status)
    }

    @Test
    fun should_get_all_pets() {
        val response = listOf(samplePetResponse())
        `when`(petService.getAllPets()).thenReturn(response)

        val result = mockMvc.get(BASE_URL)
            .andExpect {
                status { isOk() }
                jsonPath("$.size()") { value(1) }
                jsonPath("$[0].petName") { value("Neo") }
            }.andReturn()

        assertEquals(200, result.response.status)
    }

    @Test
    fun should_get_pet_by_id() {
        val response = samplePetResponse(petName = "Neo", stateLevel = 3, score = 20, customization = null)
        `when`(petService.getPetById(1L)).thenReturn(response)

        val result = mockMvc.get("$BASE_URL/1")
            .andExpect {
                status { isOk() }
                jsonPath("$.petName") { value("Neo") }
                jsonPath("$.stateLevel") { value(3) }
                jsonPath("$.score") { value(20) }
                jsonPath("$.customization").doesNotExist()
            }.andReturn()

        assertEquals(200, result.response.status)
    }

    @Test
    fun should_patch_pet_name() {
        val request = UpdatePetNameRequest(userId = 1L, petName = "Neo v2")
        val response = samplePetResponse(petName = "Neo v2")
        `when`(petService.updatePetName(1L, request)).thenReturn(response)

        val result = mockMvc.patch("$BASE_URL/1/name") {
            contentType = MediaType.APPLICATION_JSON
            content = objectMapper.writeValueAsString(request)
        }.andExpect {
            status { isOk() }
            jsonPath("$.petName") { value("Neo v2") }
        }.andReturn()

        assertEquals(200, result.response.status)
    }

    @Test
    fun should_patch_pet_customization() {
        val request = UpdatePetCustomizationRequest(userId = 1L, customization = "rojo")
        val response = samplePetResponse(customization = "rojo")
        `when`(petService.updateCustomization(1L, request)).thenReturn(response)

        val result = mockMvc.patch("$BASE_URL/1/customization") {
            contentType = MediaType.APPLICATION_JSON
            content = objectMapper.writeValueAsString(request)
        }.andExpect {
            status { isOk() }
            jsonPath("$.customization") { value("rojo") }
        }.andReturn()

        assertEquals(200, result.response.status)
    }

    @Test
    fun should_put_pet_equip() {
        val request = UpdatePetEquipRequest(userId = 1L, equippedItems = mapOf("hat" to "crown", "eyes" to "sunglasses"))
        val response = samplePetResponse(equipped = mapOf("hat" to "crown", "eyes" to "sunglasses"))
        `when`(petService.updateEquip(1L, request)).thenReturn(response)

        val result = mockMvc.put("$BASE_URL/1/equip") {
            contentType = MediaType.APPLICATION_JSON
            content = objectMapper.writeValueAsString(request)
        }.andExpect {
            status { isOk() }
            jsonPath("$.equippedItems.hat") { value("crown") }
            jsonPath("$.equippedItems.eyes") { value("sunglasses") }
        }.andReturn()

        assertEquals(200, result.response.status)
    }

    @Test
    fun should_delete_pet() {
        val result = mockMvc.delete("$BASE_URL/1")
            .andExpect { status { isNoContent() } }
            .andReturn()
        assertEquals(204, result.response.status)
    }

    @TestConfiguration
    class MockConfig {
        @Bean fun petService(): PetService = mock(PetService::class.java)
    }
}
