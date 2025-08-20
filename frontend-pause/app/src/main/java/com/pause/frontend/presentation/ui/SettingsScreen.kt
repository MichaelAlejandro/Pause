package com.pause.frontend.presentation.ui

import android.app.Application
import androidx.annotation.DrawableRes
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Logout
import androidx.compose.material.icons.filled.Pets
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Save
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.pause.frontend.presentation.ui.components.ScreenBackground
import com.pause.frontend.presentation.viewmodel.SettingsViewModel

@Composable
fun SettingsScreen(
    userId: Long,
    petId: Long?,
    onBackToLogin: () -> Unit,
    onClose: () -> Unit,
    @DrawableRes backgroundRes: Int? = null,
    onChanged: () -> Unit,
    vm: SettingsViewModel = viewModel(
        factory = viewModelFactory {
            initializer {
                val app = this[ViewModelProvider.AndroidViewModelFactory.APPLICATION_KEY] as Application
                SettingsViewModel(app)
            }
        }
    )
) {
    val ui by vm.ui.collectAsState()
    LaunchedEffect(userId, petId) { vm.load(userId, petId) }

    // Navegaciones
    LaunchedEffect(ui.doneLogout) { if (ui.doneLogout) onBackToLogin() }
    LaunchedEffect(ui.doneDeleted) { if (ui.doneDeleted) onBackToLogin() }

    // Aviso a Home para refrescar
    LaunchedEffect(ui.savedUser, ui.savedPet) {
        if (ui.savedUser || ui.savedPet) {
            onChanged()
            vm.consumeSaved()
        }
    }

    ScreenBackground(imageRes = backgroundRes, overlay = Color.Black.copy(alpha = 0.25f)) {
        if (ui.loading) {
            Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator()
            }
        } else {
            SettingsContent(
                vm = vm,
                error = ui.error,
                onClose = onClose
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun SettingsContent(
    vm: SettingsViewModel,
    error: String?,
    onClose: () -> Unit
) {
    val ui by vm.ui.collectAsState()

    // Sincroniza el texto local con lo que viene del VM cuando se carga
    var userText by remember(ui.userNameInput) { mutableStateOf(ui.userNameInput) }
    var petText by remember(ui.petNameInput)  { mutableStateOf(ui.petNameInput) }

    val surfaceGlass = MaterialTheme.colorScheme.surface.copy(alpha = 0.88f)
    val cardShape = RoundedCornerShape(24.dp)

    // Confirmación eliminar
    var confirmDelete by remember { mutableStateOf(false) }

    Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp, vertical = 16.dp),
            shape = cardShape,
            colors = CardDefaults.cardColors(containerColor = surfaceGlass),
            elevation = CardDefaults.cardElevation(defaultElevation = 6.dp)
        ) {
            Column(
                Modifier
                    .padding(horizontal = 20.dp, vertical = 16.dp)
                    .verticalScroll(rememberScrollState()),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {

                // Header
                Row(
                    Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        "Configuraciones",
                        style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold)
                    )
                    IconButton(onClick = onClose) {
                        Icon(Icons.Filled.Close, contentDescription = "Cerrar")
                    }
                }

                if (error != null) {
                    Spacer(Modifier.height(6.dp))
                    Text(error, color = MaterialTheme.colorScheme.error)
                }

                Spacer(Modifier.height(8.dp))
                Divider(color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.15f))
                Spacer(Modifier.height(12.dp))

                // -------- Usuario --------
                Text(
                    "Nombre de usuario",
                    style = MaterialTheme.typography.labelLarge,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.9f)
                )
                Spacer(Modifier.height(6.dp))
                OutlinedTextField(
                    value = userText,
                    onValueChange = {
                        userText = it
                        vm.onUserNameChange(it)
                    },
                    singleLine = true,
                    leadingIcon = { Icon(Icons.Filled.Person, contentDescription = null) },
                    shape = RoundedCornerShape(14.dp),
                    modifier = Modifier.fillMaxWidth(),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = MaterialTheme.colorScheme.primary,
                        unfocusedBorderColor = MaterialTheme.colorScheme.outline,
                        focusedContainerColor = MaterialTheme.colorScheme.surface.copy(alpha = 0.35f),
                        unfocusedContainerColor = MaterialTheme.colorScheme.surface.copy(alpha = 0.35f)
                    )
                )
                Spacer(Modifier.height(8.dp))
                Button(
                    onClick = vm::saveUserName,
                    enabled = !ui.savingUser,
                    modifier = Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.primaryContainer,
                        contentColor   = MaterialTheme.colorScheme.onPrimaryContainer
                    )
                ) {
                    if (ui.savingUser)
                        CircularProgressIndicator(strokeWidth = 2.dp, modifier = Modifier.size(18.dp))
                    else {
                        Icon(Icons.Filled.Save, contentDescription = null)
                        Spacer(Modifier.width(8.dp))
                        Text("Guardar nombre de usuario")
                    }
                }

                Spacer(Modifier.height(18.dp))
                Divider(color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.10f))
                Spacer(Modifier.height(12.dp))

                // -------- Mascota --------
                Text(
                    "Nombre de la mascota",
                    style = MaterialTheme.typography.labelLarge,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.9f)
                )
                Spacer(Modifier.height(6.dp))
                OutlinedTextField(
                    value = petText,
                    onValueChange = {
                        petText = it
                        vm.onPetNameChange(it)
                    },
                    singleLine = true,
                    leadingIcon = { Icon(Icons.Filled.Pets, contentDescription = null) },
                    shape = RoundedCornerShape(14.dp),
                    modifier = Modifier.fillMaxWidth(),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = MaterialTheme.colorScheme.primary,
                        unfocusedBorderColor = MaterialTheme.colorScheme.outline,
                        focusedContainerColor = MaterialTheme.colorScheme.surface.copy(alpha = 0.35f),
                        unfocusedContainerColor = MaterialTheme.colorScheme.surface.copy(alpha = 0.35f)
                    )
                )
                Spacer(Modifier.height(8.dp))
                Button(
                    onClick = vm::savePetName,
                    enabled = !ui.savingPet,
                    modifier = Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.primaryContainer,
                        contentColor   = MaterialTheme.colorScheme.onPrimaryContainer
                    )
                ) {
                    if (ui.savingPet)
                        CircularProgressIndicator(strokeWidth = 2.dp, modifier = Modifier.size(18.dp))
                    else {
                        Icon(Icons.Filled.Save, contentDescription = null)
                        Spacer(Modifier.width(8.dp))
                        Text("Guardar nombre de mascota")
                    }
                }

                Spacer(Modifier.height(22.dp))
                Divider(color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.10f))
                Spacer(Modifier.height(12.dp))

                // -------- Sesión --------
                Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    OutlinedButton(
                        onClick = vm::logout,
                        modifier = Modifier.weight(1f)
                    ) {
                        Icon(Icons.Filled.Logout, contentDescription = null)
                        Spacer(Modifier.width(8.dp))
                        Text("Salir del perfil")
                    }
                    Button(
                        onClick = { confirmDelete = true },
                        modifier = Modifier.weight(1f),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = MaterialTheme.colorScheme.errorContainer,
                            contentColor   = MaterialTheme.colorScheme.onErrorContainer
                        )
                    ) {
                        Icon(Icons.Filled.Delete, contentDescription = null)
                        Spacer(Modifier.width(8.dp))
                        Text("Eliminar perfil")
                    }
                }

                Spacer(Modifier.height(12.dp))
            }
        }
    }

    if (confirmDelete) {
        AlertDialog(
            onDismissRequest = { confirmDelete = false },
            confirmButton = {
                TextButton(onClick = {
                    confirmDelete = false
                    vm.deleteProfile()
                }) {
                    Text("Sí, eliminar", color = MaterialTheme.colorScheme.error)
                }
            },
            dismissButton = {
                TextButton(onClick = { confirmDelete = false }) { Text("Cancelar") }
            },
            title = { Text("¿Eliminar perfil?") },
            text  = { Text("Se eliminará tu cuenta en el servidor y los datos locales. Esta acción no se puede deshacer.") }
        )
    }
}