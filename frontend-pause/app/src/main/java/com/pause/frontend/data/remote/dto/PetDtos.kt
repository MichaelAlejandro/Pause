package com.pause.frontend.data.remote.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class CreatePetRequest(
    @SerialName("userId") val userId: Long,
    @SerialName("petName") val petName: String,
    @SerialName("customization") val customization: String? = null
)

@Serializable
data class PatchPetNameRequest(
    @SerialName("userId") val userId: Long,
    @SerialName("petName") val petName: String
)

@Serializable
data class PatchCustomizationRequest(
    @SerialName("userId") val userId: Long,
    // JSON libre serializado como texto (ej: {"color":"#00AEEF"})
    @SerialName("customization") val customization: String?
)

@Serializable
data class EquipItemsRequest(
    @SerialName("userId") val userId: Long,
    // p.ej { "head":"hat_red", "eyes":"sunglasses" }
    @SerialName("equippedItems") val equippedItems: Map<String, String>
)

@Serializable
data class PetResponse(
    @SerialName("id") val id: Long,
    @SerialName("userId") val userId: Long,
    @SerialName("petName") val petName: String,
    @SerialName("stateLevel") val stateLevel: Int,
    @SerialName("score") val score: Int,
    @SerialName("unlockedItems") val unlockedItems: List<String> = emptyList(),
    @SerialName("equippedItems") val equippedItems: Map<String, String>? = emptyMap(),
    @SerialName("customization") val customization: String? = null,
    @SerialName("lastUpdatedAt") val lastUpdatedAt: String? = null
)