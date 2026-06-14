package com.example.duelotetris.vm

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.duelotetris.PieceType
import com.example.duelotetris.TetrisConstants
import com.example.duelotetris.network.SocketManager
import com.example.duelotetris.repository.GameRepository
import com.example.duelotetris.vm.state.GameState
import com.example.duelotetris.vm.state.Piece
import com.example.duelotetris.vm.state.Screen
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.lang.System.currentTimeMillis

class GameViewModel(
    private val repository: GameRepository
) : ViewModel() {

    private val _state = MutableStateFlow(GameState())
    val state: StateFlow<GameState> = _state.asStateFlow()

    private var roomId = ""
    private var gameLoopJob: kotlinx.coroutines.Job? = null

    init {
        repository.connect()

        viewModelScope.launch {
            repository.events.collect { event ->
                when (event) {
                    is SocketManager.SocketEvent.RoomCreated -> {
                        roomId = event.roomId
                        _state.value = _state.value.copy(
                            screen = Screen.WAITING,
                            roomId = event.roomId
                        )
                    }
                    is SocketManager.SocketEvent.GameStart -> {
                        _state.value = _state.value.copy(
                            screen = Screen.GAME,
                            gameRunning = true,
                            startTime = currentTimeMillis(),
                            opponentConnected = true
                        )
                        startGameLoop()
                    }
                    is SocketManager.SocketEvent.ReceiveAttack -> {
                        addGarbageLines(event.lines)
                    }
                    is SocketManager.SocketEvent.Victory -> {
                        gameLoopJob?.cancel()
                        val duration = (currentTimeMillis() - _state.value.startTime) / 1000
                        _state.value = _state.value.copy(
                            screen = Screen.RESULT,
                            winner = true,
                            gameRunning = false,
                            duration = duration
                        )
                    }
                    is SocketManager.SocketEvent.OpponentDisconnected -> {
                        println("=== OpponentDisconnected ===")
                        gameLoopJob?.cancel()
                        val duration = (currentTimeMillis() - _state.value.startTime) / 1000
                        _state.value = _state.value.copy(
                            screen = Screen.RESULT,
                            opponentLeft = true,
                            opponentConnected = false,
                            gameRunning = false,
                            duration = duration
                        )
                    }
                    is SocketManager.SocketEvent.Error -> {
                        _state.value = _state.value.copy(errorMessage = event.message)
                    }
                }
            }
        }
    }

    fun createRoom() { repository.createRoom() }

    fun joinRoom(roomId: String) { repository.joinRoom(roomId) }

    private fun startGameLoop() {
        spawnNewPiece()

        gameLoopJob = viewModelScope.launch {
            while (_state.value.gameRunning) {
                delay(500)
                movePieceDown()
            }
        }
    }

    fun movePieceDown() {
        if (!_state.value.gameRunning) return
        val currentPiece = _state.value.currentPiece ?: return
        val newY = currentPiece.y + 1
        if (!checkCollision(currentPiece.shape, currentPiece.x, newY)) {
            _state.value = _state.value.copy(currentPiece = currentPiece.copy(y = newY))
        } else {
            mergePiece()
            checkLines()
            spawnNewPiece()
        }
    }

    fun moveLeft() {
        val piece = _state.value.currentPiece ?: return
        if (!checkCollision(piece.shape, piece.x - 1, piece.y))
            _state.value = _state.value.copy(currentPiece = piece.copy(x = piece.x - 1))
    }

    fun moveRight() {
        val piece = _state.value.currentPiece ?: return
        if (!checkCollision(piece.shape, piece.x + 1, piece.y))
            _state.value = _state.value.copy(currentPiece = piece.copy(x = piece.x + 1))
    }

    fun rotatePiece() {
        val piece = _state.value.currentPiece ?: return
        val rotated = rotateShape(piece.shape)
        if (!checkCollision(rotated, piece.x, piece.y))
            _state.value = _state.value.copy(currentPiece = piece.copy(shape = rotated))
    }

    fun hardDrop() {
        var piece = _state.value.currentPiece ?: return
        while (!checkCollision(piece.shape, piece.x, piece.y + 1))
            piece = piece.copy(y = piece.y + 1)
        _state.value = _state.value.copy(currentPiece = piece)
        mergePiece()
        checkLines()
        spawnNewPiece()
    }

    private fun rotateShape(shape: Array<IntArray>): Array<IntArray> {
        val rows = shape.size
        val cols = shape[0].size
        val rotated = Array(cols) { IntArray(rows) }
        for (i in 0 until rows)
            for (j in 0 until cols)
                rotated[j][rows - 1 - i] = shape[i][j]
        return rotated
    }

    private fun checkCollision(shape: Array<IntArray>, offsetX: Int, offsetY: Int): Boolean {
        val board = _state.value.myBoard
        for (i in shape.indices)
            for (j in shape[i].indices)
                if (shape[i][j] != 0) {
                    val x = offsetX + j
                    val y = offsetY + i
                    if (x < 0 || x >= TetrisConstants.BOARD_WIDTH || y >= TetrisConstants.BOARD_HEIGHT) return true
                    if (y >= 0 && board[y][x] != 0) return true
                }
        return false
    }

    private fun mergePiece() {
        val piece = _state.value.currentPiece ?: return
        val board = _state.value.myBoard.map { it.clone() }.toTypedArray()
        for (i in piece.shape.indices)
            for (j in piece.shape[i].indices)
                if (piece.shape[i][j] != 0) {
                    val x = piece.x + j
                    val y = piece.y + i
                    if (y >= 0 && y < TetrisConstants.BOARD_HEIGHT && x >= 0 && x < TetrisConstants.BOARD_WIDTH)
                        board[y][x] = 1
                }
        _state.value = _state.value.copy(myBoard = board)
    }

    private fun checkLines() {
        val board = _state.value.myBoard
        val newBoard = Array(TetrisConstants.BOARD_HEIGHT) { IntArray(TetrisConstants.BOARD_WIDTH) { 0 } }
        var linesCleared = 0
        var newRow = TetrisConstants.BOARD_HEIGHT - 1
        for (row in TetrisConstants.BOARD_HEIGHT - 1 downTo 0) {
            if (board[row].all { it != 0 }) linesCleared++
            else { newBoard[newRow] = board[row].copyOf(); newRow-- }
        }
        if (linesCleared > 0) {
            val newScore = _state.value.score + when (linesCleared) {
                1 -> 100; 2 -> 300; 3 -> 500; 4 -> 800; else -> 0
            }
            _state.value = _state.value.copy(
                myBoard = newBoard,
                lines = _state.value.lines + linesCleared,
                score = newScore,
                specialCounter = _state.value.specialCounter + linesCleared
            )
            val attackLines = when (linesCleared) { 2 -> 1; 3 -> 2; 4 -> 3; else -> 0}
            if (attackLines > 0 && roomId.isNotEmpty()) repository.sendAttack(roomId, attackLines)
        }
    }

    private fun addGarbageLines(count: Int) {
        val board = _state.value.myBoard.map { it.clone() }.toTypedArray()
        repeat(count) {
            for (row in 0 until TetrisConstants.BOARD_HEIGHT - 1)
                board[row] = board[row + 1].copyOf()
            val newLine = IntArray(TetrisConstants.BOARD_WIDTH) { 2 }
            newLine[(0 until TetrisConstants.BOARD_WIDTH).random()] = 0
            board[TetrisConstants.BOARD_HEIGHT - 1] = newLine
        }
        _state.value = _state.value.copy(myBoard = board)
    }

    private fun spawnNewPiece() {
        val piecesShapes = listOf(
            arrayOf(intArrayOf(1, 1, 1, 1)),
            arrayOf(intArrayOf(1, 1), intArrayOf(1, 1)),
            arrayOf(intArrayOf(0, 1, 0), intArrayOf(1, 1, 1)),
            arrayOf(intArrayOf(0, 1, 1), intArrayOf(1, 1, 0)),
            arrayOf(intArrayOf(1, 1, 0), intArrayOf(0, 1, 1)),
            arrayOf(intArrayOf(1, 0, 0), intArrayOf(1, 1, 1)),
            arrayOf(intArrayOf(0, 0, 1), intArrayOf(1, 1, 1))
        )
        val pieceTypes = listOf(PieceType.I, PieceType.O, PieceType.T, PieceType.S, PieceType.Z, PieceType.J, PieceType.L)

        val nextPiece = _state.value.nextPiece ?: run {
            val index = (0 until piecesShapes.size).random()
            val newPiece = Piece(
                type = pieceTypes[index],
                shape = piecesShapes[index],
                x = TetrisConstants.BOARD_WIDTH / 2 - piecesShapes[index][0].size / 2,
                y = 0
            )
            newPiece
        }

        val nextIndex = (0 until piecesShapes.size).random()
        val nextNewPiece = Piece(
            type = pieceTypes[nextIndex],
            shape = piecesShapes[nextIndex],
            x = TetrisConstants.BOARD_WIDTH / 2 - piecesShapes[nextIndex][0].size / 2,
            y = 0
        )

        if (checkCollision(nextPiece.shape, nextPiece.x, nextPiece.y)) {
            gameOver()
        } else {
            _state.value = _state.value.copy(currentPiece = nextPiece, nextPiece = nextNewPiece)
        }
    }

    private fun gameOver() {

        _state.value = _state.value.copy(gameRunning = false)

        if (roomId.isNotEmpty()) {
            repository.gameOver(roomId)
        }

        val duration = (currentTimeMillis() - _state.value.startTime) / 1000
        _state.value = _state.value.copy(
            screen = Screen.RESULT,
            winner = false,
            duration = duration
        )
        gameLoopJob?.cancel()
    }

    fun resetAndPlayAgain() {
        gameLoopJob?.cancel()
        _state.value = GameState()
    }
}