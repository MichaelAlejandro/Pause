package com.pause.frontend.data.repository

import com.pause.frontend.data.remote.api.Services
import com.pause.frontend.data.remote.dto.*

class SettingsRepositoryImpl : SettingsRepository {

    override suspend fun getDetails(userId: Long): Result<UserDetailResponse> = runCatching {
        val r = Services.users.getUserDetails(userId)
        if (r.isSuccessful && r.body() != null) r.body()!!
        else throw IllegalStateException("HTTP ${r.code()}: ${r.errorBody()?.string()}")
    }

    override suspend fun updateUserName(
        userId: Long,
        uid: String,
        email: String,
        newName: String
    ): Result<UserResponse> = runCatching {
        val r = Services.users.updateUser(userId, UpdateUserRequest(uid = uid, userName = newName, email = email))
        if (r.isSuccessful && r.body() != null) r.body()!!
        else throw IllegalStateException("HTTP ${r.code()}: ${r.errorBody()?.string()}")
    }

    override suspend fun updatePetName(
        petId: Long,
        userId: Long,
        newName: String
    ): Result<PetResponse> = runCatching {
        val r = Services.pets.renamePet(petId, PatchPetNameRequest(userId = userId, petName = newName))
        if (r.isSuccessful && r.body() != null) r.body()!!
        else throw IllegalStateException("HTTP ${r.code()}: ${r.errorBody()?.string()}")
    }

    override suspend fun deleteProfile(userId: Long): Result<Unit> = runCatching {
        val r = Services.users.deleteUser(userId)
        if (r.isSuccessful) Unit else throw IllegalStateException("HTTP ${r.code()}: ${r.errorBody()?.string()}")
    }
}