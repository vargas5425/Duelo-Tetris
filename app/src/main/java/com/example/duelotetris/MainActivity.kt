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
import com.example.duelotetris.ui.screens.*
import com.example.duelotetris.ui.theme.TetrisDuelTheme
import com.example.duelotetris.vm.GameViewModel
import com.example.duelotetris.vm.GameViewModelFactory

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
    val app = LocalContext.current.applicationContext as DueloTetrisApp
    val vm: GameViewModel = viewModel(
        factory = GameViewModelFactory(app.container.gameRepository)
    )

    val currentScreen by vm.currentScreen.collectAsState()

    when (currentScreen) {
        NavScreens.MENU -> MenuScreen(vm)
        NavScreens.WAITING -> WaitingScreen(vm)
        NavScreens.GAME -> GameScreen(vm)
        NavScreens.RESULT -> ResultScreen(vm)
    }
}