package com.pause.backend.controllers

import com.pause.backend.models.request.UpdatePetCustomizationRequest
import com.pause.backend.models.request.UpdatePetEquipRequest
import com.pause.backend.models.request.UpdatePetNameRequest
import com.pause.backend.models.requests.CreatePetRequest
import com.pause.backend.models.responses.PetResponse
import com.pause.backend.routes.Routes
import com.pause.backend.services.PetService
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping(Routes.BASE_URL + Routes.PETS)
class PetController(
    private val petService: PetService
) {
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    fun createPet(@RequestBody request: CreatePetRequest): PetResponse =
        petService.createPet(request)

    @GetMapping
    fun getAllPets(): List<PetResponse> = petService.getAllPets()

    @GetMapping(Routes.ID)
    fun getPetById(@PathVariable id: Long): PetResponse = petService.getPetById(id)

    @PatchMapping("${Routes.ID}/name")
    fun updatePetName(@PathVariable id: Long, @RequestBody request: UpdatePetNameRequest): PetResponse =
        petService.updatePetName(id, request)

    @PatchMapping("${Routes.ID}/customization")
    fun updateCustomization(@PathVariable id: Long, @RequestBody request: UpdatePetCustomizationRequest): PetResponse =
        petService.updateCustomization(id, request)

    @PutMapping("${Routes.ID}/equip")
    fun updateEquip(@PathVariable id: Long, @RequestBody request: UpdatePetEquipRequest): PetResponse =
        petService.updateEquip(id, request)

    @DeleteMapping(Routes.ID)
    @ResponseStatus(HttpStatus.NO_CONTENT)
    fun deletePet(@PathVariable id: Long) = petService.deletePet(id)
}
