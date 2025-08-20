package com.pause.backend.models.responses

data class UserResponse(
    val id: Long,
    val uid: String,
    val userName: String,
    val email: String
)

data class UserDetailResponse(
    val user: UserResponse,
    val pet: PetResponse?,
    val pauses: List<PauseResponse>,
    val reviews: List<ReviewResponse>
)