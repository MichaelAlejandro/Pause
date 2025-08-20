package com.pause.frontend.data.repository

import com.pause.frontend.data.remote.dto.PetResponse
import com.pause.frontend.data.remote.dto.UserResponse

interface AuthRepository {
    suspend fun createUserAndPet(email: String): Result<Pair<UserResponse, PetResponse>>
}