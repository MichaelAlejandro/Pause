package com.pause.frontend.data.repository

import com.pause.frontend.data.remote.api.Services
import com.pause.frontend.data.remote.dto.*

class AuthRepositoryImpl : AuthRepository {

    override suspend fun createUserAndPet(email: String): Result<Pair<UserResponse, PetResponse>> {
        return try {
            // 1) Intentar cargar usuario existente por email
            findExistingUserByEmail(email)?.let { existing ->
                val pet = ensurePet(existing.id)
                return Result.success(existing to pet)
            }

            // 2) No existe -> crear usuario nuevo
            val userName = email.substringBefore("@").ifBlank { "User" }
            val userRes = Services.users.createUser(
                CreateUserRequest(uid = email, userName = userName, email = email)
            )

            val user = when {
                userRes.isSuccessful && userRes.body() != null -> userRes.body()!!
                // 2.b Fallback si el backend devuelve Conflict (ya existe)
                userRes.code() == 409 -> {
                    findExistingUserByEmail(email)
                        ?: return Result.failure(IllegalStateException("El usuario ya existe pero no se pudo obtener."))
                }
                else -> return Result.failure(IllegalStateException("Error usuario: ${userRes.code()} ${userRes.errorBody()?.string()}"))
            }

            // 3) Asegurar mascota "nano"
            val pet = ensurePet(user.id)
            Result.success(user to pet)

        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    // ---------- helpers ----------

    private suspend fun findExistingUserByEmail(email: String): UserResponse? {
        val listRes = Services.users.listUsers()
        if (!listRes.isSuccessful || listRes.body() == null) return null
        return listRes.body()!!.firstOrNull { it.email.equals(email, ignoreCase = true) }
    }

    private suspend fun ensurePet(userId: Long): PetResponse {
        // intentar traer detalles para ver si ya tiene pet
        val details = Services.users.getUserDetails(userId)
        val existingPet = if (details.isSuccessful) details.body()?.pet else null
        if (existingPet != null) return existingPet

        // crear mascota por defecto "nano" (stateLevel lo fija el backend en 3)
        val petRes = Services.pets.createPet(
            CreatePetRequest(userId = userId, petName = "nano", customization = null)
        )
        if (!petRes.isSuccessful || petRes.body() == null) {
            throw IllegalStateException("Error mascota: ${petRes.code()} ${petRes.errorBody()?.string()}")
        }
        return petRes.body()!!
    }
}