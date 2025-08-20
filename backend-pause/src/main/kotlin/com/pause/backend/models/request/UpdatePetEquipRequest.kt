package com.pause.backend.models.request

data class UpdatePetEquipRequest(
    val userId: Long,
    val equippedItems: Map<String, String>
)