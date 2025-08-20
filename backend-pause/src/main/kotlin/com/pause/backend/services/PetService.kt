package com.pause.backend.services

import com.pause.backend.exceptions.exceptions.InvalidRequestException
import com.pause.backend.exceptions.exceptions.ResourceNotFoundException
import com.pause.backend.mappers.toResponse
import com.pause.backend.models.entities.Pet
import com.pause.backend.models.request.UpdatePetCustomizationRequest
import com.pause.backend.models.request.UpdatePetEquipRequest
import com.pause.backend.models.request.UpdatePetNameRequest
import com.pause.backend.models.requests.CreatePetRequest
import com.pause.backend.models.responses.PetResponse
import com.pause.backend.repositories.PetRepository
import com.pause.backend.repositories.UserRepository
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.time.LocalDateTime

@Service
class PetService(
    private val petRepository: PetRepository,
    private val userRepository: UserRepository
) {

    /** Slots permitidos y sus ítems válidos */
    private val validSlots = setOf("head", "eyes")

    private val itemsBySlot: Map<String, Set<String>> = mapOf(
        "head" to setOf("hat_red", "hat_blue", "crown"),
        "eyes" to setOf("sunglasses")
    )

    @Transactional
    fun createPet(request: CreatePetRequest): PetResponse {
        if (request.petName.isBlank()) {
            throw InvalidRequestException(
                "petName must not be blank",
                mapOf("petName" to "required")
            )
        }

        val user = userRepository.findById(request.userId).orElseThrow {
            ResourceNotFoundException("User not found (id=${request.userId})")
        }

        // El estado inicial oficial lo maneja el backend
        val pet = Pet(
            user = user,
            petName = request.petName,
            stateLevel = 3,
            score = 0,
            lastUpdatedAt = LocalDateTime.now(),
            customization = request.customization
        )
        return petRepository.save(pet).toResponse()
    }

    fun getPetById(id: Long): PetResponse =
        petRepository.findById(id).orElseThrow {
            ResourceNotFoundException("Pet with ID $id not found")
        }.toResponse()

    fun getAllPets(): List<PetResponse> =
        petRepository.findAll().map { it.toResponse() }

    /** Cambiar nombre */
    @Transactional
    fun updatePetName(id: Long, request: UpdatePetNameRequest): PetResponse {
        if (request.petName.isBlank()) {
            throw InvalidRequestException(
                "petName must not be blank",
                mapOf("petName" to "required")
            )
        }
        val pet = petRepository.findById(id).orElseThrow {
            ResourceNotFoundException("Pet with ID $id not found")
        }
        pet.petName = request.petName
        pet.lastUpdatedAt = LocalDateTime.now()
        return petRepository.save(pet).toResponse()
    }

    /**
     * Equipar/unequipar ítems (PATCH semántico):
     * - fusiona con lo ya equipado (no borra otros slots)
     * - "" (o null/blank) => des-equipar ese slot
     * - valida slot e items por slot
     * - valida que el item esté desbloqueado
     */
    @Transactional
    fun updateEquip(id: Long, request: UpdatePetEquipRequest): PetResponse {
        val pet = petRepository.findById(id).orElseThrow {
            ResourceNotFoundException("Pet with ID $id not found")
        }

        // Copia del estado actual
        val merged = pet.equippedItems.toMutableMap()

        request.equippedItems.forEach { (rawSlot, rawItemId) ->
            val slot = rawSlot.trim()
            if (slot !in validSlots) {
                throw InvalidRequestException(
                    "Invalid slot '$slot'",
                    mapOf("slot" to slot, "allowed" to validSlots.joinToString(","))
                )
            }

            val itemId = rawItemId?.trim().orEmpty()
            if (itemId.isBlank()) {
                // Unequip
                merged.remove(slot)
                return@forEach
            }

            val allowed = itemsBySlot[slot] ?: emptySet()
            if (itemId !in allowed) {
                throw InvalidRequestException(
                    "Item '$itemId' is not valid for slot '$slot'",
                    mapOf("slot" to slot, "itemId" to itemId, "allowed" to allowed.joinToString(","))
                )
            }

            // Validar que el item esté desbloqueado para esta mascota
            if (itemId !in pet.unlockedItems) {
                throw InvalidRequestException(
                    "Item '$itemId' not unlocked for this pet",
                    mapOf("itemId" to itemId)
                )
            }

            merged[slot] = itemId
        }

        // Asegurar que no se cuelen slots desconocidos
        merged.keys.retainAll(validSlots)

        pet.equippedItems.clear()
        pet.equippedItems.putAll(merged)
        pet.lastUpdatedAt = LocalDateTime.now()
        return petRepository.save(pet).toResponse()
    }

    /** Guardar personalización libre (JSON/Texto) */
    @Transactional
    fun updateCustomization(id: Long, request: UpdatePetCustomizationRequest): PetResponse {
        val pet = petRepository.findById(id).orElseThrow {
            ResourceNotFoundException("Pet with ID $id not found")
        }
        pet.customization = request.customization
        pet.lastUpdatedAt = LocalDateTime.now()
        return petRepository.save(pet).toResponse()
    }

    @Transactional
    fun deletePet(id: Long) {
        val pet = petRepository.findById(id).orElseThrow {
            ResourceNotFoundException("Pet with ID $id not found")
        }
        petRepository.delete(pet)
    }
}