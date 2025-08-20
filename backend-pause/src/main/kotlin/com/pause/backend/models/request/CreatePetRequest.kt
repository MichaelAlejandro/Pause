package com.pause.backend.models.requests

data class CreatePetRequest(
    val userId: Long,
    val petName: String,
    val customization: String? = null
)
