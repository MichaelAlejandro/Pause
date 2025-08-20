package com.pause.frontend.presentation.ui.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.pause.frontend.R

@Composable
fun PetSprite(
    stateLevel: Int,
    head: String? = null,     // "hat_red" | "hat_blue" | "crown"
    eyes: String? = null      // "sunglasses"
) {
    val base = when (stateLevel.coerceIn(1, 5)) {
        1 -> R.drawable.pet_devastated
        2 -> R.drawable.pet_sad
        3 -> R.drawable.pet_neutral
        4 -> R.drawable.pet_happy
        else -> R.drawable.pet_excited
    }

    Box(contentAlignment = Alignment.Center) {
        Image(painterResource(base), contentDescription = null, modifier = Modifier.size(220.dp))

        // head items
        val headRes = when (head) {
            "hat_red" -> R.drawable.hat_red
            "hat_blue" -> R.drawable.hat_blue
            "crown" -> R.drawable.crown
            else -> null
        }
        headRes?.let {
            Image(
                painterResource(it),
                contentDescription = null,
                modifier = Modifier
                    .size(220.dp)
            )
        }

        // eyes items
        if (eyes == "sunglasses") {
            Image(
                painterResource(R.drawable.sunglasses),
                contentDescription = null,
                modifier = Modifier.size(220.dp)
            )
        }
    }
}