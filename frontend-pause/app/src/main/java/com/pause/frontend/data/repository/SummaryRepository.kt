package com.pause.frontend.data.repository

import com.pause.frontend.data.remote.dto.SummaryResponse

interface SummaryRepository {
    suspend fun getSummary(userId: Long): Result<SummaryResponse>
}