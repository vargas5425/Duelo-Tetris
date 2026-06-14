package com.example.duelotetris.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.duelotetris.vm.GameViewModel

@Composable
fun WaitingScreen(vm: GameViewModel) {
    val state by vm.state.collectAsState()

    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        CircularProgressIndicator()
        Spacer(modifier = Modifier.height(16.dp))
        Text("Esperando oponente...")
        Spacer(modifier = Modifier.height(8.dp))
        Text("Código de sala: ${state.roomId}", style = MaterialTheme.typography.titleMedium)
        Spacer(modifier = Modifier.height(32.dp))
        Button(onClick = { vm.resetAndPlayAgain() }) {
            Text("Cancelar")
        }
    }
}