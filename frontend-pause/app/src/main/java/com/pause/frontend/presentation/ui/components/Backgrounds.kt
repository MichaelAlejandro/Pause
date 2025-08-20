package com.pause.frontend.presentation.ui.components

import androidx.annotation.DrawableRes
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource

@Composable
fun ScreenBackground(
    @DrawableRes imageRes: Int? = null,
    color: Color = Color.White,
    overlay: Color? = null,
    content: @Composable BoxScope.() -> Unit
) {
    Box(Modifier.fillMaxSize().background(color)) {
        if (imageRes != null) {
            Image(
                painter = painterResource(imageRes),
                contentDescription = null,
                modifier = Modifier.fillMaxSize(),
                contentScale = ContentScale.Crop
            )
        }
        if (overlay != null) Box(Modifier.fillMaxSize().background(overlay))
        content()
    }
}