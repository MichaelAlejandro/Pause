package com.pause.frontend.presentation.navigation

import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.navigation.NavController
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Checkroom
import androidx.compose.material.icons.filled.PauseCircle
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Quiz
import androidx.compose.material.icons.filled.Settings
import com.pause.frontend.data.local.SessionPrefs
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.pause.frontend.ui.theme.Surface70

private data class BottomItem(
    val id: String,              // para selected
    val icon: @Composable () -> Unit,
    val buildRoute: (userId: Long?, petId: Long?) -> String
)

@Composable
fun BottomNavBar(navController: NavController, modifier: Modifier = Modifier) {
    val ctx = LocalContext.current
    val prefs = remember { SessionPrefs(ctx) }
    val userId by prefs.userIdFlow.collectAsState(initial = null)
    val petId by prefs.petIdFlow.collectAsState(initial = null)

    val items = remember {
        listOf(
            BottomItem("wardrobe", { Icon(Icons.Filled.Checkroom, contentDescription = "Vestuario") }) { u, p ->
                if (u != null && p != null) "${Routes.Wardrobe}/$u/$p" else Routes.Login
            },
            BottomItem("pause", { Icon(Icons.Filled.PauseCircle, contentDescription = "Pausas") }) { u, _ ->
                if (u != null) "${Routes.Pause}/$u" else Routes.Login
            },
            BottomItem("home", { Icon(Icons.Filled.Home, contentDescription = "Home") }) { u, _ ->
                if (u != null) "${Routes.Home}/$u" else Routes.Login
            },
            BottomItem("review", { Icon(Icons.Filled.Quiz, contentDescription = "Repasos") }) { u, _ ->
                if (u != null) "${Routes.Review}/$u" else Routes.Login
            },
            BottomItem("settings", { Icon(Icons.Filled.Settings, contentDescription = "Ajustes") }) { u, p ->
                if (u != null && p != null) "${Routes.Settings}/$u/$p"
                else if (u != null) "${Routes.Settings}/$u"
                else Routes.Login
            },
        )
    }

    val backStack by navController.currentBackStackEntryAsState()
    val currentRoute = backStack?.destination?.route.orEmpty()

    NavigationBar(
        modifier = modifier,
        containerColor = Surface70,
        contentColor = MaterialTheme.colorScheme.onSurface,
        tonalElevation = 0.dp
    ) {
        items.forEach { item ->
            val selected = currentRoute.startsWith(item.id) ||
                    currentRoute.startsWith("${Routes.Wardrobe}/").takeIf { item.id=="wardrobe" } == true ||
                    currentRoute.startsWith("${Routes.Pause}/").takeIf { item.id=="pause" } == true ||
                    currentRoute.startsWith("${Routes.Home}/").takeIf { item.id=="home" } == true ||
                    currentRoute.startsWith("${Routes.Review}/").takeIf { item.id=="review" } == true ||
                    (currentRoute.startsWith("${Routes.Settings}/").takeIf { item.id=="settings" } == true)

            NavigationBarItem(
                selected = selected,
                onClick = {
                    val target = item.buildRoute(userId, petId)
                    navController.navigate(target) {
                        launchSingleTop = true
                        restoreState = true
                        popUpTo(navController.graph.startDestinationId) { saveState = true }
                    }
                },
                icon = item.icon,
                alwaysShowLabel = false,
                label = {}
            )
        }
    }
}