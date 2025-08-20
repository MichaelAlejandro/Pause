package com.pause.frontend.data.repository

import com.pause.frontend.data.remote.api.Services
import com.pause.frontend.data.remote.dto.SummaryResponse

class SummaryRepositoryImpl : SummaryRepository {
    override suspend fun getSummary(userId: Long): Result<SummaryResponse> {
        return try {
            val res = Services.summary.getSummary(userId)
            if (res.isSuccessful) {
                val body = res.body()
                if (body != null) Result.success(body)
                else Result.failure(IllegalStateException("Respuesta vacía"))
            } else {
                Result.failure(IllegalStateException("HTTP ${res.code()}: ${res.errorBody()?.string()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}