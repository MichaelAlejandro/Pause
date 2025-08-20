package com.pause.frontend.data.repository

import com.pause.frontend.data.remote.dto.PetResponse
import com.pause.frontend.data.remote.dto.SummaryResponse

interface WardrobeRepository {
    suspend fun loadSummary(userId: Long): Result<SummaryResponse>
    suspend fun equip(petId: Long, userId: Long, equipped: Map<String, String>): Result<PetResponse>
}