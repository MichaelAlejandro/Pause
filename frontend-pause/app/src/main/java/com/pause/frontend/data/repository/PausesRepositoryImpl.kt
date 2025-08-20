package com.pause.frontend.data.repository

import com.pause.frontend.data.remote.api.Services
import com.pause.frontend.data.remote.dto.CreatePauseRequest
import com.pause.frontend.data.remote.dto.PauseResponse

class PausesRepositoryImpl : PausesRepository {
    override suspend fun createPause(body: CreatePauseRequest): Result<PauseResponse> {
        return try {
            val res = Services.pauses.createPause(body)
            if (res.isSuccessful && res.body() != null) Result.success(res.body()!!)
            else Result.failure(IllegalStateException("HTTP ${res.code()}: ${res.errorBody()?.string()}"))
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}