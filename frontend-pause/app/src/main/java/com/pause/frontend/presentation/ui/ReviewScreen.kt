package com.pause.frontend.presentation.ui

import androidx.annotation.DrawableRes
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Autorenew
import androidx.compose.material.icons.outlined.Check
import androidx.compose.material.icons.outlined.Quiz
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.pause.frontend.presentation.ui.components.ScreenBackground
import com.pause.frontend.presentation.viewmodel.ReviewViewModel

@Composable
fun ReviewScreen(
    userId: Long,
    onFinished: () -> Unit,
    @DrawableRes backgroundRes: Int? = null,
    vm: ReviewViewModel = viewModel()
) {
    val ui by vm.ui.collectAsState()

    if (ui.done) LaunchedEffect(Unit) { onFinished() }

    ScreenBackground(imageRes = backgroundRes, overlay = null) {
        // Scrim tenue
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
                elevation = CardDefaults.elevatedCardElevation(defaultElevation = 8.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp)
            ) {
                Column(
                    Modifier.padding(20.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Row(
                        Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            "Repaso",
                            style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold)
                        )
                        AssistChip(
                            onClick = { /* sin acción */ },
                            label = { Text(ui.q?.topic ?: "") },
                            leadingIcon = { Icon(Icons.Outlined.Quiz, null) }
                        )
                    }

                    Spacer(Modifier.height(12.dp))

                    Text(
                        ui.q?.statement ?: "",
                        style = MaterialTheme.typography.titleMedium
                    )

                    Spacer(Modifier.height(12.dp))

                    // Opciones (chips seleccionables)
                    Column(
                        verticalArrangement = Arrangement.spacedBy(8.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        val q = ui.q
                        if (q != null) {
                            q.options.forEachIndexed { idx, opt ->
                                FilterChip(
                                    selected = ui.selected == idx,
                                    onClick = { vm.select(idx) },
                                    label = { Text(opt) }
                                )
                            }
                        }
                    }

                    if (ui.error != null) {
                        Spacer(Modifier.height(10.dp))
                        Text(ui.error!!, color = MaterialTheme.colorScheme.error)
                    }

                    Spacer(Modifier.height(16.dp))

                    Row(
                        horizontalArrangement = Arrangement.spacedBy(12.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Button(
                            onClick = { vm.submit(userId) },
                            enabled = ui.selected != null && !ui.sending,
                            modifier = Modifier.weight(1f),
                            shape = RoundedCornerShape(999.dp)
                        ) {
                            if (ui.sending) {
                                CircularProgressIndicator(
                                    strokeWidth = 2.dp,
                                    modifier = Modifier.size(18.dp)
                                )
                            } else {
                                Icon(Icons.Outlined.Check, contentDescription = null)
                                Spacer(Modifier.width(8.dp))
                                Text("Enviar")
                            }
                        }

                        OutlinedButton(
                            onClick = { vm.shuffleQuestion() },
                            enabled = !ui.sending,
                            modifier = Modifier.weight(1f),
                            shape = RoundedCornerShape(999.dp)
                        ) {
                            Icon(Icons.Outlined.Autorenew, contentDescription = null)
                            Spacer(Modifier.width(8.dp))
                            Text("Cambiar pregunta")
                        }
                    }

                    Spacer(Modifier.height(8.dp))

                    OutlinedButton(
                        onClick = onFinished,
                        enabled = !ui.sending,
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(999.dp)
                    ) {
                        Text("Cancelar")
                    }
                }
            }
        }
    }
}
