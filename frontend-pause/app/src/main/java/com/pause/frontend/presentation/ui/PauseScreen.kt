package com.pause.frontend.presentation.ui

import androidx.annotation.DrawableRes
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Autorenew
import androidx.compose.material.icons.outlined.Lightbulb
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.pause.frontend.presentation.ui.components.ScreenBackground
import com.pause.frontend.presentation.viewmodel.PauseViewModel

@Composable
fun PauseScreen(
    userId: Long,
    onFinished: () -> Unit,
    @DrawableRes backgroundRes: Int? = null,
    vm: PauseViewModel = viewModel()
) {
    val ui by vm.ui.collectAsState()

    LaunchedEffect(Unit) { vm.start() }
    if (ui.done) LaunchedEffect(Unit) { onFinished() }

    // Ideas/sugerencias para la pausa (puedes ajustar o traducir a tu gusto)
    val tips = remember {
        listOf(
            "Bebe un vaso de agua",
            "Mira a lo lejos 20s para descansar la vista",
            "Respira 4-7-8: inhala 4s, retén 7s, exhala 8s",
            "Estira cuello y hombros suavemente",
            "Camina un minuto y vuelve",
            "Cierra los ojos 20s y relaja la mandíbula"
        )
    }
    var tip by remember { mutableStateOf(tips.random()) }
    fun nextTip() {
        tip = tips.shuffled().first { it != tip }
    }

    ScreenBackground(imageRes = backgroundRes, overlay = null) {
        // Scrim suave por si no hay overlay en el fondo
        Box(
            Modifier
                .fillMaxSize()
                .background(Color.Black.copy(alpha = 0.35f))
        )

        Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            ElevatedCard(
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.elevatedCardColors(
                    containerColor = MaterialTheme.colorScheme.surface.copy(alpha = 0.96f)
                ),
                elevation = CardDefaults.elevatedCardElevation(defaultElevation = 8.dp)
            ) {
                Column(
                    Modifier.padding(20.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        "Pausa activa",
                        style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold)
                    )

                    Spacer(Modifier.height(12.dp))

                    // Píldora del tiempo restante
                    Surface(
                        shape = RoundedCornerShape(999.dp),
                        color = MaterialTheme.colorScheme.primaryContainer,
                        tonalElevation = 1.dp
                    ) {
                        Text(
                            "Tiempo restante: ${ui.secondsLeft}s",
                            modifier = Modifier.padding(horizontal = 14.dp, vertical = 6.dp),
                            color = MaterialTheme.colorScheme.onPrimaryContainer,
                            style = MaterialTheme.typography.labelLarge
                        )
                    }

                    Spacer(Modifier.height(12.dp))

                    // Sugerencia de actividad para la pausa
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        AssistChip(
                            onClick = { /* sin acción */ },
                            label = { Text(tip) },
                            leadingIcon = {
                                Icon(
                                    imageVector = Icons.Outlined.Lightbulb,
                                    contentDescription = null
                                )
                            }
                        )
                        // Botón redondo para cambiar de sugerencia
                        Surface(
                            shape = CircleShape,
                            color = MaterialTheme.colorScheme.secondaryContainer
                        ) {
                            IconButton(onClick = { nextTip() }) {
                                Icon(
                                    imageVector = Icons.Outlined.Autorenew,
                                    contentDescription = "Otra idea"
                                )
                            }
                        }
                    }

                    Spacer(Modifier.height(16.dp))

                    Row(
                        horizontalArrangement = Arrangement.spacedBy(12.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Button(
                            onClick = { vm.finishAndSend(userId) },
                            modifier = Modifier.weight(1f),
                            shape = RoundedCornerShape(999.dp)
                        ) {
                            Text(if (ui.secondsLeft > 0) "Terminar ahora" else "Registrar pausa")
                        }
                        OutlinedButton(
                            onClick = { vm.cancel(); onFinished() },
                            modifier = Modifier.weight(1f),
                            shape = RoundedCornerShape(999.dp)
                        ) {
                            Text("Cancelar")
                        }
                    }

                    if (ui.error != null) {
                        Spacer(Modifier.height(10.dp))
                        Text(ui.error!!, color = MaterialTheme.colorScheme.error)
                    }
                }
            }
        }
    }
}