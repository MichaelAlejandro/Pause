package com.pause.frontend.data.repository

import com.pause.frontend.data.remote.api.Services
import com.pause.frontend.data.remote.dto.EquipItemsRequest
import com.pause.frontend.data.remote.dto.PetResponse
import com.pause.frontend.data.remote.dto.SummaryResponse

class WardrobeRepositoryImpl : WardrobeRepository {
    override suspend fun loadSummary(userId: Long): Result<SummaryResponse> = runCatching {
        val res = Services.summary.getSummary(userId)
        if (res.isSuccessful && res.body() != null) res.body()!!
        else throw IllegalStateException("HTTP ${res.code()}: ${res.errorBody()?.string()}")
    }

    override suspend fun equip(
        petId: Long,
        userId: Long,
        equipped: Map<String, String>
    ): Result<PetResponse> = runCatching {
        val res = Services.pets.equipItems(petId, EquipItemsRequest(userId = userId, equippedItems = equipped))
        if (res.isSuccessful && res.body() != null) res.body()!!
        else throw IllegalStateException("HTTP ${res.code()}: ${res.errorBody()?.string()}")
    }
}