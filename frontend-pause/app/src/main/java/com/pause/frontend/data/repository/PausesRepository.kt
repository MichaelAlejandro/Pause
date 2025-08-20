package com.pause.frontend.data.repository

import com.pause.frontend.data.remote.dto.CreatePauseRequest
import com.pause.frontend.data.remote.dto.PauseResponse

interface PausesRepository {
    suspend fun createPause(body: CreatePauseRequest): Result<PauseResponse>
}