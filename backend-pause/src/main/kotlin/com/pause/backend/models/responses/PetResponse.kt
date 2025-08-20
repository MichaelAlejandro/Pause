package com.pause.backend.models.responses

import java.time.LocalDateTime

data class PetResponse(
    val id: Long,
    val userId: Long,
    val petName: String,
    val stateLevel: Int,
    val score: Int,
    val unlockedItems: Set<String>,
    val equippedItems: Map<String, String>,
    val customization: String?,
    val lastUpdatedAt: LocalDateTime
)
