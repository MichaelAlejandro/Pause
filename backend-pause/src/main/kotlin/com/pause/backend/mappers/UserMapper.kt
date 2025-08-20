package com.pause.backend.mappers

import com.pause.backend.models.entities.User
import com.pause.backend.models.responses.UserDetailResponse
import com.pause.backend.models.responses.UserResponse

fun User.toResponse(): UserResponse =
    UserResponse(
        id = requireNotNull(this.id),
        uid = this.uid,
        userName = this.userName,
        email = this.email
    )

fun User.toDetailResponse(): UserDetailResponse =
    UserDetailResponse(
        user = this.toResponse(),
        pet = this.pet?.toResponse(),
        pauses = this.pauses.map { it.toResponse() },
        reviews = this.reviews.map { it.toResponse() }
    )
