package com.pause.frontend.presentation.ui

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.constraintlayout.compose.Dimension
import androidx.lifecycle.viewmodel.compose.viewModel
import com.pause.frontend.R
import com.pause.frontend.presentation.ui.components.PetSprite
import com.pause.frontend.presentation.ui.components.ScreenBackground
import com.pause.frontend.presentation.viewmodel.HomeUiState
import com.pause.frontend.presentation.viewmodel.HomeViewModel

@Composable
fun HomeScreen(
    userId: Long,
    onGoPause: () -> Unit = {},
    onGoReview: () -> Unit = {},
    onGoWardrobe: () -> Unit = {},
    refreshKey: Int = 0,
    onGoSettings: () -> Unit = {},
    vm: HomeViewModel = viewModel()
) {
    val ui by vm.ui.collectAsState()

    LaunchedEffect(userId, refreshKey) { vm.load(userId) }

    ScreenBackground(
        imageRes = R.drawable.bg_home,
        overlay = Color.Black.copy(alpha = 0.28f)
    ) {
        when {
            ui.loading -> Loading()
            ui.error != null -> ErrorBox(ui.error ?: "Error") { vm.retry(userId) }
            ui.data != null -> ContentMinimal(ui)
        }
    }
}

@Composable private fun Loading() {
    Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) { CircularProgressIndicator() }
}

@Composable private fun ErrorBox(msg: String, onRetry: () -> Unit) {
    Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text("Ups: $msg"); Spacer(Modifier.height(8.dp)); Button(onClick = onRetry) { Text("Reintentar") }
        }
    }
}

@Composable
private fun ContentMinimal(ui: HomeUiState) {
    val s = ui.data!!

    val userName = s.userName ?: ""
    val petName  = s.petName ?: ""

    val level = s.stateLevel
    val head = s.equippedItems?.get("head")
    val eyes = s.equippedItems?.get("eyes")

    ConstraintLayout(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp)
    ) {
        val (headerRef, petRef) = createRefs()

        // ------- Encabezado sobre las cortinas -------
        Column(
            modifier = Modifier.constrainAs(headerRef) {
                start.linkTo(parent.start)
                end.linkTo(parent.end)
                top.linkTo(parent.top)
                bottom.linkTo(parent.bottom)
                width = Dimension.fillToConstraints
                verticalBias = 0.12f   // ajusta 0.10–0.18 para posicionarlo sobre las cortinas
            },
            horizontalAlignment = Alignment.Start
        ) {
            Text(
                text = "Bienvenido/a $userName",
                style = MaterialTheme.typography.titleLarge.copy(
                    fontSize = 22.sp,
                    fontWeight = FontWeight.Bold
                ),
                color = MaterialTheme.colorScheme.onBackground,
                textAlign = TextAlign.Center
            )
            Spacer(Modifier.height(4.dp))
            Text(
                text = "$petName – Nivel $level",
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.9f),
                textAlign = TextAlign.Center
            )
        }

        // ------- Mascota sobre la alfombra -------
        Box(
            modifier = Modifier.constrainAs(petRef) {
                start.linkTo(parent.start)
                end.linkTo(parent.end)
                top.linkTo(parent.top)
                bottom.linkTo(parent.bottom)
                verticalBias = 0.80f    // 0.84–0.90 para clavarla en la alfombra
            },
            contentAlignment = Alignment.Center
        ) {
            PetSprite(
                stateLevel = level,
                head = head,
                eyes = eyes
                // PetSprite no acepta modifier; por eso la envolvemos en un Box posicionado
            )
        }
    }
}
