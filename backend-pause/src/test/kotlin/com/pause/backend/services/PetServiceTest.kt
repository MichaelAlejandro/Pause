package com.pause.backend.services

import com.pause.backend.exceptions.exceptions.InvalidRequestException
import com.pause.backend.exceptions.exceptions.ResourceNotFoundException
import com.pause.backend.models.entities.Pet
import com.pause.backend.models.entities.User
import com.pause.backend.models.request.UpdatePetCustomizationRequest
import com.pause.backend.models.request.UpdatePetEquipRequest
import com.pause.backend.models.request.UpdatePetNameRequest
import com.pause.backend.models.requests.CreatePetRequest
import com.pause.backend.repositories.PetRepository
import com.pause.backend.repositories.UserRepository
import org.junit.jupiter.api.*
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.assertThrows
import org.mockito.Mockito.*
import java.time.LocalDateTime
import java.util.*

class PetServiceTest {

    private lateinit var petRepository: PetRepository
    private lateinit var userRepository: UserRepository
    private lateinit var petService: PetService

    @BeforeEach
    fun setUp() {
        petRepository = mock(PetRepository::class.java)
        userRepository = mock(UserRepository::class.java)
        petService = PetService(petRepository, userRepository)
    }

    @Test
    fun should_create_a_pet() {
        val user = User(uid = "uid123", userName = "Michael", email = "maalejandro@puce.edu")
        val request = CreatePetRequest(userId = 1L, petName = "Pausito", customization = "verde")

        val pet = Pet(
            user = user,
            petName = request.petName,
            stateLevel = 3,
            score = 0,
            lastUpdatedAt = LocalDateTime.now(),
            customization = request.customization
        )

        `when`(userRepository.findById(1L)).thenReturn(Optional.of(user))
        `when`(petRepository.save(any(Pet::class.java))).thenReturn(pet)

        val result = petService.createPet(request)

        assertEquals("Pausito", result.petName)
        assertEquals("verde", result.customization)
        assertEquals(3, result.stateLevel)
        assertEquals(0, result.score)
    }

    @Test
    fun should_throw_exception_when_user_not_found_on_create() {
        val request = CreatePetRequest(1L, "Pet", null)
        `when`(userRepository.findById(1L)).thenReturn(Optional.empty())
        assertThrows<ResourceNotFoundException> { petService.createPet(request) }
    }

    @Test
    fun should_return_pet_by_id() {
        val user = User(uid = "uid123", userName = "Michael", email = "maalejandro@puce.edu")
        val pet = Pet(user = user, petName = "Pausito", stateLevel = 3, score = 0, lastUpdatedAt = LocalDateTime.now(), customization = "rojo")
        `when`(petRepository.findById(1L)).thenReturn(Optional.of(pet))

        val result = petService.getPetById(1L)
        assertEquals("Pausito", result.petName)
    }

    @Test
    fun should_throw_exception_when_pet_not_found_by_id() {
        `when`(petRepository.findById(1L)).thenReturn(Optional.empty())
        assertThrows<ResourceNotFoundException> { petService.getPetById(1L) }
    }

    @Test
    fun should_return_all_pets() {
        val user = User(uid = "uid123", userName = "Michael", email = "maalejandro@puce.edu")
        val pet = Pet(user = user, petName = "Pausito", stateLevel = 3, score = 0, lastUpdatedAt = LocalDateTime.now(), customization = "rojo")
        `when`(petRepository.findAll()).thenReturn(listOf(pet))

        val result = petService.getAllPets()
        assertEquals(1, result.size)
        assertEquals("Pausito", result[0].petName)
    }

    @Test
    fun should_update_pet_name() {
        val user = User(uid = "uid123", userName = "Michael", email = "maalejandro@puce.edu")
        val pet = Pet(user = user, petName = "Viejo", stateLevel = 3, score = 0, lastUpdatedAt = LocalDateTime.now(), customization = "gris")
        `when`(petRepository.findById(1L)).thenReturn(Optional.of(pet))
        `when`(petRepository.save(pet)).thenReturn(pet)

        val result = petService.updatePetName(1L, UpdatePetNameRequest(userId = 1L, petName = "Nuevo"))
        assertEquals("Nuevo", result.petName)
    }

    @Test
    fun should_throw_when_updating_name_blank() {
        val user = User(uid = "uid123", userName = "Michael", email = "maalejandro@puce.edu")
        val pet = Pet(user = user, petName = "Nombre", stateLevel = 3, score = 0, lastUpdatedAt = LocalDateTime.now(), customization = "x")
        `when`(petRepository.findById(1L)).thenReturn(Optional.of(pet))

        assertThrows<InvalidRequestException> {
            petService.updatePetName(1L, UpdatePetNameRequest(userId = 1L, petName = ""))
        }
    }

    @Test
    fun should_update_customization() {
        val user = User(uid = "uid123", userName = "Michael", email = "maalejandro@puce.edu")
        val pet = Pet(user = user, petName = "P", stateLevel = 3, score = 0, lastUpdatedAt = LocalDateTime.now(), customization = "a")
        `when`(petRepository.findById(1L)).thenReturn(Optional.of(pet))
        `when`(petRepository.save(pet)).thenReturn(pet)

        val result = petService.updateCustomization(1L,
            UpdatePetCustomizationRequest(userId = 1L, customization = "{\"color\":\"#00AEEF\"}")
        )
        assertEquals("{\"color\":\"#00AEEF\"}", result.customization)
    }

    @Test
    fun should_update_equip_items() {
        val user = User(uid = "uid123", userName = "Michael", email = "maalejandro@puce.edu")
        val pet = Pet(user = user, petName = "P", stateLevel = 3, score = 0, lastUpdatedAt = LocalDateTime.now(), customization = null)
        `when`(petRepository.findById(1L)).thenReturn(Optional.of(pet))
        `when`(petRepository.save(pet)).thenReturn(pet)

        val result = petService.updateEquip(1L,
            UpdatePetEquipRequest(userId = 1L, equippedItems = mapOf("head" to "hat_red"))
        )
        assertEquals("hat_red", result.equippedItems["head"])
    }

    @Test
    fun should_throw_exception_when_deleting_non_existent_pet() {
        `when`(petRepository.findById(1L)).thenReturn(Optional.empty())
        assertThrows<ResourceNotFoundException> { petService.deletePet(1L) }
    }
}