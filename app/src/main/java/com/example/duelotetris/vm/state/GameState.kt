package com.example.duelotetris.vm.state

import com.example.duelotetris.PieceType

data class GameState(
    var screen: Screen = Screen.MENU,
    var roomId: String = "",
    var myBoard: Array<IntArray> = Array(20) { IntArray(10) { 0 } },
    var opponentBoard: Array<IntArray> = Array(20) { IntArray(10) { 0 } },
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
    var startTime: Long = 0L
)

enum class Screen {
    MENU, WAITING, GAME, RESULT
}

data class Piece(
    val type: PieceType,
    val shape: Array<IntArray>,
    val x: Int,
    val y: Int
)