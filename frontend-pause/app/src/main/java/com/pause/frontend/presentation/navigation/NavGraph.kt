package com.pause.frontend.presentation.navigation

import androidx.compose.runtime.*
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.pause.frontend.data.local.SessionPrefs
import com.pause.frontend.presentation.ui.*
import androidx.compose.material3.Scaffold
import androidx.compose.ui.Modifier
import androidx.compose.foundation.layout.padding
import androidx.navigation.compose.currentBackStackEntryAsState
import com.pause.frontend.R

object Routes {
    const val Splash = "splash"
    const val Login = "login"
    const val Home = "home"
    const val Pause = "pause"
    const val Review = "review"
    const val Wardrobe = "wardrobe"
    const val Settings = "settings"
}

@Composable
fun AppNavGraph(navController: NavHostController = rememberNavController()) {
    val backStack by navController.currentBackStackEntryAsState()
    val route = backStack?.destination?.route.orEmpty()

    // Ocultar barra en Splash y Login
    val showBar = remember(route) {
        route.startsWith(Routes.Home) ||
                route.startsWith(Routes.Pause) ||
                route.startsWith(Routes.Review) ||
                route.startsWith(Routes.Wardrobe) ||
                route.startsWith(Routes.Settings)
    }

    Scaffold(
        bottomBar = { if (showBar) BottomNavBar(navController) }
    ) { innerPadding ->
        val ctx = LocalContext.current
        NavHost(
            navController = navController,
            startDestination = Routes.Splash,
            modifier = Modifier.padding(innerPadding)
        ) {
            // --- Splash ---
            composable(Routes.Splash) {
                val prefs = SessionPrefs(ctx)
                val userId by prefs.userIdFlow.collectAsState(initial = null)
                LaunchedEffect(userId) {
                    if (userId == null) {
                        navController.navigate(Routes.Login) {
                            popUpTo(Routes.Splash) { inclusive = true }
                        }
                    } else {
                        navController.navigate("${Routes.Home}/$userId") {
                            popUpTo(Routes.Splash) { inclusive = true }
                        }
                    }
                }
            }

            // --- Login ---
            composable(Routes.Login) {
                LoginScreen(onDone = {
                    navController.navigate(Routes.Splash) {
                        popUpTo(Routes.Login) { inclusive = true }
                    }
                })
            }

            // --- Home ---
            composable("${Routes.Home}/{userId}") { back ->
                val ctx2 = LocalContext.current
                val prefs = remember { SessionPrefs(ctx2) }
                val userId = back.arguments?.getString("userId")?.toLongOrNull() ?: 1L
                val petId by prefs.petIdFlow.collectAsState(initial = null)

                // refreshKey y flags (wardrobe/settings/progress) si ya lo tenías, mantenlos
                var refreshKey by remember { mutableStateOf(0) }
                val wardrobeChanged by remember {
                    navController.currentBackStackEntry!!
                        .savedStateHandle.getStateFlow("wardrobe_changed", false)
                }.collectAsState(initial = false)
                val settingsChanged by remember {
                    navController.currentBackStackEntry!!
                        .savedStateHandle.getStateFlow("settings_changed", false)
                }.collectAsState(initial = false)
                val progressChanged by remember {
                    navController.currentBackStackEntry!!
                        .savedStateHandle.getStateFlow("progress_changed", false)
                }.collectAsState(initial = false)

                LaunchedEffect(wardrobeChanged, settingsChanged, progressChanged) {
                    if (wardrobeChanged || settingsChanged || progressChanged) {
                        refreshKey++
                        navController.currentBackStackEntry?.savedStateHandle?.set("wardrobe_changed", false)
                        navController.currentBackStackEntry?.savedStateHandle?.set("settings_changed", false)
                        navController.currentBackStackEntry?.savedStateHandle?.set("progress_changed", false)
                    }
                }

                HomeScreen(
                    userId = userId,
                    refreshKey = refreshKey,
                    onGoPause = { navController.navigate("${Routes.Pause}/$userId") },
                    onGoReview = { navController.navigate("${Routes.Review}/$userId") },
                    onGoWardrobe = { petId?.let { navController.navigate("${Routes.Wardrobe}/$userId/$it") } },
                    onGoSettings = {
                        if (petId != null) navController.navigate("${Routes.Settings}/$userId/$petId")
                        else navController.navigate("${Routes.Settings}/$userId")
                    }
                )
            }

            // --- Pause ---
            composable("${Routes.Pause}/{userId}") { back ->
                val userId = back.arguments?.getString("userId")?.toLongOrNull() ?: 1L
                PauseScreen(
                    userId = userId,
                    backgroundRes = R.drawable.bg_pause,
                    onFinished = {
                        navController.previousBackStackEntry?.savedStateHandle?.set("progress_changed", true)
                        navController.popBackStack()
                    }
                )
            }

            // --- Review ---
            composable("${Routes.Review}/{userId}") { back ->
                val userId = back.arguments?.getString("userId")?.toLongOrNull() ?: 1L
                ReviewScreen(
                    userId = userId,
                    backgroundRes = R.drawable.bg_review,
                    onFinished = {
                        navController.previousBackStackEntry?.savedStateHandle?.set("progress_changed", true)
                        navController.popBackStack()
                    }
                )
            }

            // --- Wardrobe ---
            composable("${Routes.Wardrobe}/{userId}/{petId}") { back ->
                val userId = back.arguments?.getString("userId")?.toLongOrNull() ?: 1L
                val petId = back.arguments?.getString("petId")?.toLongOrNull() ?: 1L
                WardrobeScreen(
                    userId = userId,
                    petId = petId,
                    backgroundRes = R.drawable.bg_wardrobe,
                    onClose = { navController.popBackStack() },
                    onEquipped = {
                        navController.previousBackStackEntry?.savedStateHandle?.set("wardrobe_changed", true)
                        navController.popBackStack()
                    }
                )
            }

            // --- Settings (dos rutas: con y sin petId) ---
            composable("${Routes.Settings}/{userId}") { back ->
                val userId = back.arguments?.getString("userId")?.toLongOrNull() ?: 1L
                SettingsScreen(
                    userId = userId,
                    petId = null,
                    backgroundRes = R.drawable.bg_wardrobe,
                    onBackToLogin = {
                        navController.navigate(Routes.Login) {
                            popUpTo(Routes.Home) { inclusive = true }
                        }
                    },
                    onClose = { navController.popBackStack() },
                    onChanged = {
                        navController.previousBackStackEntry?.savedStateHandle?.set("settings_changed", true)
                    }
                )
            }
            composable("${Routes.Settings}/{userId}/{petId}") { back ->
                val userId = back.arguments?.getString("userId")?.toLongOrNull() ?: 1L
                val petId = back.arguments?.getString("petId")?.toLongOrNull()
                SettingsScreen(
                    userId = userId,
                    petId = petId,
                    backgroundRes = R.drawable.bg_settings,
                    onBackToLogin = {
                        navController.navigate(Routes.Login) {
                            popUpTo(Routes.Home) { inclusive = true }
                        }
                    },
                    onClose = { navController.popBackStack() },
                    onChanged = {
                        navController.previousBackStackEntry?.savedStateHandle?.set("settings_changed", true)
                    }
                )
            }
        }
    }
}