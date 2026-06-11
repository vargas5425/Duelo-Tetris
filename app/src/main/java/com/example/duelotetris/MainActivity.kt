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
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.duelotetris.ui.screens.NavScreens
import com.example.duelotetris.ui.screens.GameScreen
import com.example.duelotetris.ui.screens.MenuScreen
import com.example.duelotetris.ui.screens.ResultScreen
import com.example.duelotetris.ui.screens.WaitingScreen
import com.example.duelotetris.ui.theme.TetrisDuelTheme
import com.example.duelotetris.vm.GameViewModel
import com.example.duelotetris.vm.GameViewModelFactory
import com.example.duelotetris.vm.state.Screen

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            TetrisDuelTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    TetrisDuelApp(modifier = Modifier.padding(innerPadding))
                }
            }
        }
    }
}

@Composable
fun TetrisDuelApp(modifier: Modifier = Modifier) {
    var currentScreen by remember { mutableStateOf(NavScreens.MENU) }

    val app = LocalContext.current.applicationContext as DueloTetrisApp
    val vm: GameViewModel = viewModel(
        factory = GameViewModelFactory(app.container.gameRepository)
    )

    val state by vm.state.collectAsState()

    LaunchedEffect(state.screen) {
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