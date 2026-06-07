package com.example.duelotetris

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import com.example.duelotetris.ui.screens.*
import com.example.duelotetris.ui.theme.TetrisDuelTheme
import com.example.duelotetris.vm.GameViewModel
import com.example.duelotetris.vm.state.Screen

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            TetrisDuelTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    TetrisDuelApp(
                        modifier = Modifier.padding(innerPadding)
                    )
                }
            }
        }
    }
}

@Composable
fun TetrisDuelApp(modifier: Modifier = Modifier) {
    var currentScreen by remember { mutableStateOf(NavScreens.MENU) }
    val vm = remember { GameViewModel() }
    val state by vm.state.collectAsState()  // ← IMPORTANTE: Observar el estado

    // ← NUEVO: Observar cambios en state.screen para navegar automáticamente
    LaunchedEffect(state.screen) {
        println("TetrisDuelApp - screen cambió a: ${state.screen}")
        when (state.screen) {
            Screen.MENU -> currentScreen = NavScreens.MENU
            Screen.WAITING -> currentScreen = NavScreens.WAITING
            Screen.GAME -> currentScreen = NavScreens.GAME
            Screen.RESULT -> currentScreen = NavScreens.RESULT
        }
    }

    when (currentScreen) {
        NavScreens.MENU -> MenuScreen(vm) { currentScreen = it }
        NavScreens.WAITING -> WaitingScreen(vm) { currentScreen = it }
        NavScreens.GAME -> GameScreen(vm) { currentScreen = it }
        NavScreens.RESULT -> ResultScreen(vm) { currentScreen = it }
    }
}