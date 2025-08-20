package com.pause.backend.mappers

import com.pause.backend.models.entities.Pet
import com.pause.backend.models.responses.PetResponse

fun Pet.toResponse(): PetResponse =
    PetResponse(
        id = requireNotNull(this.id),
        userId = requireNotNull(this.user.id),
        petName = this.petName,
        stateLevel = this.stateLevel,          // 1..5
        score = this.score,
        unlockedItems = this.unlockedItems,
        equippedItems = this.equippedItems,
        customization = this.customization,
        lastUpdatedAt = this.lastUpdatedAt
    )