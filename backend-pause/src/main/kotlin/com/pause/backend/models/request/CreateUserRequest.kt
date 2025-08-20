package com.pause.backend.models.requests

import com.pause.backend.models.entities.User

data class CreateUserRequest(
    val uid: String,
    val userName: String,
    val email: String
) {
    fun toEntity(): User {
        return User(
            uid = uid,
            userName = userName,
            email = email
        )
    }
}
