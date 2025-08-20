package com.pause.frontend.presentation.ui

import android.app.Application
import androidx.annotation.DrawableRes
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shadow
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.constraintlayout.compose.Dimension
import com.pause.frontend.R
import com.pause.frontend.presentation.ui.components.ScreenBackground
import com.pause.frontend.presentation.viewmodel.LoginViewModel
import com.pause.frontend.ui.theme.NeonCyan
import com.pause.frontend.ui.theme.PauseTypography

@Composable
fun LoginScreen(
    onDone: () -> Unit,
    @DrawableRes backgroundRes: Int? = R.drawable.bg_login,   // <- imagen de fondo
    vm: LoginViewModel = viewModel(
        factory = viewModelFactory {
            initializer {
                val app = this[ViewModelProvider.AndroidViewModelFactory.APPLICATION_KEY] as Application
                LoginViewModel(app)
            }
        }
    )
) {
    val ui by vm.ui.collectAsState()

    LaunchedEffect(ui.done) { if (ui.done) onDone() }

    ScreenBackground(
        imageRes = backgroundRes ?: R.drawable.bg_login,
        overlay = Color.Black.copy(alpha = 0.30f) // mejora contraste
    ) {
        ConstraintLayout(
            Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp)
                .imePadding()
        ) {
            val (titleRef, cardRef) = createRefs()

            Spacer(Modifier.height(30.dp))
            Text(
                text = "P.A.U.S.E.",
                style = PauseTypography.titleLarge.copy(
                    fontSize = 48.sp,
                    color = NeonCyan,
                    shadow = Shadow(
                        color = NeonCyan.copy(alpha = 0.85f),
                        blurRadius = 18f
                    )
                ),
                modifier = Modifier.constrainAs(titleRef) {
                    top.linkTo(parent.top)
                    start.linkTo(parent.start)
                    end.linkTo(parent.end)
                    bottom.linkTo(parent.bottom)
                    verticalBias = 0.23f   // ~altura del letrero
                }
            )

            // ===== Formulario centrado sobre la "puerta" =====
            Card(
                modifier = Modifier
                    .constrainAs(cardRef) {
                        start.linkTo(parent.start)
                        end.linkTo(parent.end)
                        top.linkTo(parent.top)
                        bottom.linkTo(parent.bottom)
                        verticalBias = 0.48f     // ~zona de la puerta
                        width = Dimension.percent(0.58f)
                    },
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surfaceVariant, // translúcido
                    contentColor = MaterialTheme.colorScheme.onSurface
                ),
                elevation = CardDefaults.cardElevation(defaultElevation = 6.dp)
            ) {
                Column(
                    Modifier.padding(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text("Registrar o Iniciar Sesión", style = MaterialTheme.typography.titleMedium)
                    Spacer(Modifier.height(8.dp))

                    var tf by remember { mutableStateOf(TextFieldValue(ui.email)) }
                    OutlinedTextField(
                        value = tf,
                        onValueChange = { tf = it; vm.onEmailChange(it.text) },
                        label = { Text("Correo") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )

                    if (ui.error != null) {
                        Spacer(Modifier.height(8.dp))
                        Text(ui.error!!, color = MaterialTheme.colorScheme.error)
                    }

                    Spacer(Modifier.height(12.dp))
                    Button(
                        onClick = { vm.createProfile() },
                        enabled = ui.valid && !ui.loading,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        if (ui.loading)
                            CircularProgressIndicator(strokeWidth = 2.dp, modifier = Modifier.size(18.dp))
                        else
                            Text("Continuar")
                    }
                }
            }
        }
    }
}