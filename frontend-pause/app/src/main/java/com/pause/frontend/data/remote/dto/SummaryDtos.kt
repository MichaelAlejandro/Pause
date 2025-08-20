package com.pause.frontend.data.remote.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class SummaryResponse(
    @SerialName("userId") val userId: Long,
    @SerialName("stateLevel") val stateLevel: Int,
    @SerialName("score") val score: Int,
    @SerialName("recentPausesCount") val recentPausesCount: Int,
    @SerialName("recentReviewsCount") val recentReviewsCount: Int,
    @SerialName("unlockedItems") val unlockedItems: List<String> = emptyList(),
    @SerialName("equippedItems") val equippedItems: Map<String, String>? = emptyMap(),
    @SerialName("nextUnlocks") val nextUnlocks: List<NextUnlock> = emptyList(),
    @SerialName("lastUpdatedAt") val lastUpdatedAt: String,
    @SerialName("userName") val userName: String? = null,
    @SerialName("petName")  val petName: String? = null
)

@Serializable
data class NextUnlock(
    @SerialName("itemId") val itemId: String,
    @SerialName("requirementText") val requirementText: String,
    @SerialName("progress") val progress: Int,
    @SerialName("required") val required: Int
)