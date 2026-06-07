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
import com.example.duelotetris.ui.screens.GameScreen
import com.example.duelotetris.ui.screens.MenuScreen
import com.example.duelotetris.ui.screens.NavScreens
import com.example.duelotetris.ui.screens.ResultScreen
import com.example.duelotetris.ui.screens.WaitingScreen
import com.example.duelotetris.ui.theme.TetrisDuelTheme
import com.example.duelotetris.vm.GameViewModel


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

    when (currentScreen) {
        NavScreens.MENU -> MenuScreen(vm) { currentScreen = it }
        NavScreens.WAITING -> WaitingScreen(vm) { currentScreen = it }
        NavScreens.GAME -> GameScreen(vm) { currentScreen = it }
        NavScreens.RESULT -> ResultScreen(vm) { currentScreen = it }
    }
}