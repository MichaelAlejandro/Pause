package com.pause.frontend.ui.theme

import androidx.compose.material3.Typography
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp

val PauseTypography = Typography(
    titleLarge  = androidx.compose.ui.text.TextStyle(fontSize = 22.sp, fontWeight = FontWeight.SemiBold),
    titleMedium = androidx.compose.ui.text.TextStyle(fontSize = 18.sp, fontWeight = FontWeight.Medium),
    bodyLarge   = androidx.compose.ui.text.TextStyle(fontSize = 16.sp),
    bodyMedium  = androidx.compose.ui.text.TextStyle(fontSize = 14.sp),
    labelLarge  = androidx.compose.ui.text.TextStyle(fontSize = 14.sp, fontWeight = FontWeight.Medium)
)