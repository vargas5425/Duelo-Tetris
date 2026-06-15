package com.example.duelotetris.vm.state

import com.example.duelotetris.PieceType
import com.example.duelotetris.TetrisConstants


data class GameState(
    var roomId: String = "",
    var myBoard: Array<IntArray> = Array(TetrisConstants.BOARD_HEIGHT) { IntArray(TetrisConstants.BOARD_WIDTH)},
    var currentPiece: Piece? = null,
    var nextPiece: Piece? = null,
    var score: Int = 0,
    var lines: Int = 0,
    var gameRunning: Boolean = false,
    var winner: Boolean = false,
    var opponentLeft: Boolean = false,
    var errorMessage: String? = null,
    var specialCounter: Int = 0,
    var duration: Long = 0L,
    var startTime: Long = 0L,
    var opponentConnected: Boolean = false
)

data class Piece(
    val type: PieceType,
    val shape: Array<IntArray>,
    val x: Int,
    val y: Int
)