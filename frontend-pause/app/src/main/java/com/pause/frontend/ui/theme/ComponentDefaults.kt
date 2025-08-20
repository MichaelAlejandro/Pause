package com.pause.frontend.ui.theme

import androidx.compose.material3.CardColors
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable

@Composable
fun translucentCardColors(): CardColors = CardDefaults.cardColors(
    containerColor = MaterialTheme.colorScheme.surfaceVariant, // Surface90
    contentColor = MaterialTheme.colorScheme.onSurface
)

@Composable
fun translucentDialogColors(): CardColors = CardDefaults.cardColors(
    containerColor = Surface70,
    contentColor = MaterialTheme.colorScheme.onSurface
)