package com.pause.backend.models.responses

import java.time.LocalDateTime

data class SummaryResponse(
    val userId: Long,
    val stateLevel: Int,
    val score: Int,
    val recentPausesCount: Int,
    val recentReviewsCount: Int,
    val unlockedItems: Set<String>,
    val equippedItems: Map<String, String>,
    val nextUnlocks: List<UnlockPreview>,
    val lastUpdatedAt: LocalDateTime,
    val userName: String?,
    val petName: String?
)