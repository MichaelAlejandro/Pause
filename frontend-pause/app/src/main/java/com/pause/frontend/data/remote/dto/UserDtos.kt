package com.pause.frontend.data.remote.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class CreateUserRequest(
    @SerialName("uid") val uid: String,
    @SerialName("userName") val userName: String,
    @SerialName("email") val email: String
)

@Serializable
data class UpdateUserRequest(
    @SerialName("uid") val uid: String,
    @SerialName("userName") val userName: String,
    @SerialName("email") val email: String
)

@Serializable
data class UserResponse(
    @SerialName("id") val id: Long,
    @SerialName("uid") val uid: String,
    @SerialName("userName") val userName: String,
    @SerialName("email") val email: String
)

@Serializable
data class UserDetailResponse(
    @SerialName("user") val user: UserResponse,
    @SerialName("pet") val pet: PetResponse? = null,
    @SerialName("pauses") val pauses: List<PauseResponse> = emptyList(),
    @SerialName("reviews") val reviews: List<ReviewResponse> = emptyList()
)