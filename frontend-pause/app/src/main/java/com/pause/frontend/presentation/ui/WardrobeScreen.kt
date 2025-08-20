package com.pause.frontend.presentation.ui

import androidx.annotation.DrawableRes
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.pause.frontend.presentation.ui.components.PetAssets
import com.pause.frontend.presentation.ui.components.PetSprite
import com.pause.frontend.presentation.ui.components.ScreenBackground
import com.pause.frontend.presentation.viewmodel.WardrobeUiState
import com.pause.frontend.presentation.viewmodel.WardrobeViewModel

@Composable
fun WardrobeScreen(
    userId: Long,
    petId: Long,
    onClose: () -> Unit,
    onEquipped: () -> Unit,
    @DrawableRes backgroundRes: Int? = null,
    vm: WardrobeViewModel = viewModel()
) {
    val ui by vm.ui.collectAsState()
    LaunchedEffect(userId, petId) { vm.load(userId, petId) }

    ScreenBackground(imageRes = backgroundRes, overlay = Color.Black.copy(alpha = 0.25f)) {
        when {
            ui.loading -> CenterLoading()
            ui.error != null -> CenterError(ui.error!!, onClose)
            ui.saved -> {
                // Vuelve a Home/vestuario anterior cuando el backend confirme
                LaunchedEffect(Unit) { onEquipped() }
                CenterLoading()
            }
            else -> Content(ui, vm::select, vm::save, onClose)
        }
    }
}

@Composable private fun CenterLoading() {
    Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) { CircularProgressIndicator() }
}

@Composable private fun CenterError(msg: String, onClose: () -> Unit) {
    Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text("Error: $msg")
            Spacer(Modifier.height(8.dp))
            Button(onClick = onClose) { Text("Cerrar") }
        }
    }
}

@Composable
private fun Content(
    ui: WardrobeUiState,
    onSelect: (slot: String, itemId: String?) -> Unit,
    onSave: () -> Unit,
    onClose: () -> Unit
) {
    val s = ui.summary!!
    val headItems = PetAssets.filterBySlot(s.unlockedItems, "head")
    val eyesItems = PetAssets.filterBySlot(s.unlockedItems, "eyes")

    Box(Modifier.fillMaxSize()) {

        // ---------- Opciones ARRIBA ----------
        Column(
            modifier = Modifier
                .align(Alignment.TopCenter)
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 12.dp)
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                "Vestuario",
                style = MaterialTheme.typography.headlineSmall,
                color = Color.White
            )
            Spacer(Modifier.height(8.dp))

            SlotSection(
                title = "Cabeza",
                options = headItems,
                selected = ui.selectedHead,
                onSelect = { item -> onSelect("head", item) }
            )

            Spacer(Modifier.height(12.dp))

            SlotSection(
                title = "Ojos",
                options = eyesItems,
                selected = ui.selectedEyes,
                onSelect = { item -> onSelect("eyes", item) }
            )

            Spacer(Modifier.height(16.dp))
            Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                Button(onClick = onSave, enabled = ui.hasChanges && !ui.saving) {
                    if (ui.saving)
                        CircularProgressIndicator(strokeWidth = 2.dp, modifier = Modifier.size(18.dp))
                    else
                        Text("Guardar cambios")
                }
                OutlinedButton(onClick = onClose) { Text("Cancelar") }
            }
        }

        // ---------- Mascota ABAJO (sobre la alfombra) ----------
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .align(Alignment.BottomCenter)
                .padding(bottom = 28.dp), // ajusta 24–40dp para clavarla en la alfombra
            contentAlignment = Alignment.Center
        ) {
            PetSprite(
                stateLevel = s.stateLevel,
                head = ui.selectedHead,
                eyes = ui.selectedEyes
            )
        }
    }
}

@Composable
private fun SlotSection(
    title: String,
    options: List<String>,
    selected: String?,
    onSelect: (String?) -> Unit
) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(title, style = MaterialTheme.typography.titleMedium, color = Color.White)
        Spacer(Modifier.height(8.dp))

        // Chip “Ninguno” + chips de items centrados
        FlowRowMainAxisSpacing {
            FilterChip(
                selected = selected == null,
                onClick = { onSelect(null) },
                label = { Text("Ninguno") }
            )
            Spacer(Modifier.width(8.dp))

            options.forEach { item ->
                FilterChip(
                    selected = selected == item,
                    onClick = { onSelect(if (selected == item) null else item) },
                    label = { Text(PetAssets.label(item)) }
                )
                Spacer(Modifier.width(8.dp))
            }
        }
    }
}

/** Helper sencillo para centrar los chips */
@Composable
private fun FlowRowMainAxisSpacing(content: @Composable RowScope.() -> Unit) {
    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.Center) {
        content()
    }
}