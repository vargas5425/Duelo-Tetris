package com.example.duelotetris.ui.screens

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.windowInsetsPadding
import com.example.duelotetris.TetrisConstants
import com.example.duelotetris.vm.GameViewModel
import com.example.duelotetris.vm.state.Piece
import com.example.duelotetris.vm.state.Screen

@Composable
fun GameScreen(vm: GameViewModel, onNavigate: (NavScreens) -> Unit) {
    val state by vm.state.collectAsState()

    LaunchedEffect(state.screen) {
        if (state.screen == Screen.RESULT) {
            onNavigate(NavScreens.RESULT)
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(8.dp)
            .windowInsetsPadding(WindowInsets.navigationBars)
    ) {
        // Información superior (Score, Lines, 37)
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text("Score", style = MaterialTheme.typography.labelSmall)
                Text("${state.score}", style = MaterialTheme.typography.titleLarge)
            }
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text("Lines", style = MaterialTheme.typography.labelSmall)
                Text("${state.lines}", style = MaterialTheme.typography.titleLarge)
            }
            if (state.specialCounter >= TetrisConstants.SPECIAL_NUMBER) {
                Surface(
                    color = Color.Red,
                    shape = MaterialTheme.shapes.small
                ) {
                    Text(
                        " 37 ",
                        color = Color.White,
                        style = MaterialTheme.typography.titleMedium
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        // ===== SOLO TU TABLERO (pantalla completa) =====
        Column(
            modifier = Modifier.weight(1f),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text("TU TABLERO", style = MaterialTheme.typography.titleMedium)
            Spacer(modifier = Modifier.height(4.dp))
            BoardCanvas(
                board = state.myBoard,
                piece = state.currentPiece,
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
                    .background(Color.Black)
            )
        }

        Spacer(modifier = Modifier.height(8.dp))

        // Siguiente pieza
        Row(
            modifier = Modifier.fillMaxWidth().padding(8.dp),
            horizontalArrangement = Arrangement.Center
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text("Siguiente:", style = MaterialTheme.typography.labelSmall)
                state.nextPiece?.let {
                    SmallBoardCanvas(
                        piece = it,
                        modifier = Modifier
                            .width(80.dp)
                            .height(80.dp)
                    )
                }
            }
        }

        // Controles
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp)
                .padding(bottom = 16.dp),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            Button(onClick = { vm.moveLeft() }, modifier = Modifier.size(60.dp, 50.dp)) { Text("←") }
            Button(onClick = { vm.moveRight() }, modifier = Modifier.size(60.dp, 50.dp)) { Text("→") }
            Button(onClick = { vm.rotatePiece() }, modifier = Modifier.size(60.dp, 50.dp)) { Text("↻") }
            Button(onClick = { vm.hardDrop() }, modifier = Modifier.size(60.dp, 50.dp)) { Text("↓ ↓") }
        }
    }
}

// BoardCanvas - Tablero principal
@Composable
fun BoardCanvas(board: Array<IntArray>, piece: Piece?, modifier: Modifier = Modifier) {
    Canvas(modifier = modifier) {
        val cellW = size.width / TetrisConstants.BOARD_WIDTH
        val cellH = size.height / TetrisConstants.BOARD_HEIGHT

        for (row in board.indices) {
            for (col in board[row].indices) {
                if (board[row][col] != 0) {
                    drawRect(
                        color = Color(0xFF00FF00),
                        topLeft = Offset(col * cellW, row * cellH),
                        size = Size(cellW, cellH)
                    )
                    drawRect(
                        color = Color.Black,
                        topLeft = Offset(col * cellW, row * cellH),
                        size = Size(cellW, cellH),
                        style = androidx.compose.ui.graphics.drawscope.Stroke(width = 1f)
                    )
                }
            }
        }

        piece?.let {
            for (i in it.shape.indices) {
                for (j in it.shape[i].indices) {
                    if (it.shape[i][j] != 0) {
                        val x = (it.x + j) * cellW
                        val y = (it.y + i) * cellH
                        drawRect(
                            color = Color(0xFFFF0000),
                            topLeft = Offset(x, y),
                            size = Size(cellW, cellH)
                        )
                        drawRect(
                            color = Color.Black,
                            topLeft = Offset(x, y),
                            size = Size(cellW, cellH),
                            style = androidx.compose.ui.graphics.drawscope.Stroke(width = 1f)
                        )
                    }
                }
            }
        }
    }
}

// SmallBoardCanvas - Vista previa
@Composable
fun SmallBoardCanvas(piece: Piece, modifier: Modifier = Modifier) {
    Canvas(modifier = modifier.background(Color.Black)) {
        val cellW = size.width / 4
        val cellH = size.height / 4

        for (i in piece.shape.indices) {
            for (j in piece.shape[i].indices) {
                if (piece.shape[i][j] != 0) {
                    drawRect(
                        color = Color(0xFFFF0000),
                        topLeft = Offset(j * cellW, i * cellH),
                        size = Size(cellW, cellH)
                    )
                }
            }
        }
    }
}