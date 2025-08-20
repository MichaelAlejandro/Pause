package com.pause.backend.models.request

data class UpdatePetNameRequest(
    val userId: Long,
    val petName: String
)