package com.pause.frontend.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable

private val PauseDarkColors = darkColorScheme(
    primary = NeonCyan,
    onPrimary = androidx.compose.ui.graphics.Color.Black,
    primaryContainer = NeonCyan.copy(alpha = 0.22f),
    onPrimaryContainer = OnDarkHigh,

    secondary = NeonPurple,
    onSecondary = androidx.compose.ui.graphics.Color.Black,
    secondaryContainer = NeonPurple.copy(alpha = 0.22f),
    onSecondaryContainer = OnDarkHigh,

    tertiary = NeonPink,
    onTertiary = androidx.compose.ui.graphics.Color.Black,
    tertiaryContainer = NeonPink.copy(alpha = 0.22f),
    onTertiaryContainer = OnDarkHigh,

    background = Ink,
    onBackground = OnDarkHigh,
    surface = InkAlt,
    onSurface = OnDarkHigh,
    surfaceVariant = Surface90,
    onSurfaceVariant = OnDarkHigh,

    outline = OnDarkLow,
    outlineVariant = OnDarkMedium,

    error = Danger,
    onError = androidx.compose.ui.graphics.Color.Black,
    errorContainer = Danger.copy(alpha = 0.18f),
    onErrorContainer = OnDarkHigh
)

/** Theme oscuro fijo, pensado para tus fondos neon. */
@Composable
fun PauseTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = PauseDarkColors,
        typography = PauseTypography,
        shapes = PauseShapes,
        content = content
    )
}