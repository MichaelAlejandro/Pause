package com.pause.frontend.data.repository

import com.pause.frontend.data.remote.dto.*

interface SettingsRepository {
    suspend fun getDetails(userId: Long): Result<UserDetailResponse>
    suspend fun updateUserName(userId: Long, uid: String, email: String, newName: String): Result<UserResponse>
    suspend fun updatePetName(petId: Long, userId: Long, newName: String): Result<PetResponse>
    suspend fun deleteProfile(userId: Long): Result<Unit>
}