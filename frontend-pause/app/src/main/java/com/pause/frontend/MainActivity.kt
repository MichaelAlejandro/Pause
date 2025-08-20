package com.pause.frontend

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.material3.Surface
import com.pause.frontend.presentation.navigation.AppNavGraph
import com.pause.frontend.ui.theme.PauseTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            PauseTheme {
                Surface { AppNavGraph() }
            }
        }
    }
}