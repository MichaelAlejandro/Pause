package com.pause.frontend.presentation.ui.components

import androidx.annotation.DrawableRes
import com.pause.frontend.R

object PetAssets {
    // Mapea un itemId → slot
    fun slotOf(itemId: String): String? = when (itemId) {
        "hat_red", "hat_blue", "crown" -> "head"
        "sunglasses" -> "eyes"
        else -> null
    }

    // Mapea itemId → drawable
    @DrawableRes
    fun drawableOf(itemId: String): Int? = when (itemId) {
        "hat_red" -> R.drawable.hat_red
        "hat_blue" -> R.drawable.hat_blue
        "crown" -> R.drawable.crown
        "sunglasses" -> R.drawable.sunglasses
        else -> null
    }

    fun label(itemId: String): String = when (itemId) {
        "hat_red" -> "Gorro rojo"
        "hat_blue" -> "Gorro azul"
        "crown" -> "Corona"
        "sunglasses" -> "Gafas"
        else -> itemId
    }

    val slots = listOf("head", "eyes")

    fun filterBySlot(unlocked: List<String>, slot: String): List<String> =
        unlocked.filter { slotOf(it) == slot }
}