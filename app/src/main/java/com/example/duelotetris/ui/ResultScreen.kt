package com.example.duelotetris.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.duelotetris.TetrisConstants
import com.example.duelotetris.vm.GameViewModel

@Composable
fun ResultScreen(vm: GameViewModel, onNavigate: (NavScreens) -> Unit) {
    val state by vm.state.collectAsState()

    Column(
        modifier = Modifier.fillMaxSize().padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = if (state.winner) "¡VICTORIA!" else if (state.opponentLeft) "OPONENTE DESCONECTADO" else "DERROTA",
            style = MaterialTheme.typography.headlineMedium
        )

        Spacer(modifier = Modifier.height(32.dp))

        Card(
            modifier = Modifier.fillMaxWidth(),
            elevation = CardDefaults.cardElevation(4.dp)
        ) {
            Column(
                modifier = Modifier.padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text("Puntaje: ${state.score}", style = MaterialTheme.typography.titleLarge)
                Text("Líneas eliminadas: ${state.lines}", style = MaterialTheme.typography.titleMedium)
                Text("Duración: ${state.duration} segundos", style = MaterialTheme.typography.titleMedium)
                if (state.specialCounter >= TetrisConstants.SPECIAL_NUMBER) {
                    Text("Lograste el 37!", style = MaterialTheme.typography.titleSmall)
                }
            }
        }

        Spacer(modifier = Modifier.height(32.dp))

        Button(
            onClick = { vm.resetAndPlayAgain(); onNavigate(NavScreens.MENU) },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Volver al Menú")
        }
    }
}