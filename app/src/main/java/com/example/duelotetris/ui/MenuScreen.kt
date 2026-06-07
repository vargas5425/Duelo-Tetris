package com.example.duelotetris.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.duelotetris.vm.GameViewModel

@Composable
fun MenuScreen(vm: GameViewModel, onNavigate: (NavScreens) -> Unit) {
    val state by vm.state.collectAsState()
    var roomIdInput by remember { mutableStateOf("") }
    val snackbarHostState = remember { SnackbarHostState() }

    // Mostrar error si existe
    LaunchedEffect(state.errorMessage) {
        state.errorMessage?.let { error ->
            snackbarHostState.showSnackbar(error)
            // Limpiar el error después de mostrarlo (opcional)
            // vm.clearError()
        }
    }

    Scaffold(
        snackbarHost = { SnackbarHost(hostState = snackbarHostState) }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text("Tetris Duel", style = MaterialTheme.typography.headlineLarge)

            Spacer(modifier = Modifier.height(32.dp))

            Button(
                onClick = { vm.createRoom() },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Crear Sala")
            }

            Spacer(modifier = Modifier.height(16.dp))

            OutlinedTextField(
                value = roomIdInput,
                onValueChange = { roomIdInput = it.uppercase() },
                label = { Text("Código de Sala") },
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(8.dp))

            Button(
                onClick = { vm.joinRoom(roomIdInput) },
                modifier = Modifier.fillMaxWidth(),
                enabled = roomIdInput.isNotBlank()
            ) {
                Text("Unirse a Sala")
            }
        }
    }
}