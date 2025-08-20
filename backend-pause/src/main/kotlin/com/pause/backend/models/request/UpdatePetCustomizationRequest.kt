package com.pause.backend.models.request

data class UpdatePetCustomizationRequest(
    val userId: Long,
    val customization: String?
)
