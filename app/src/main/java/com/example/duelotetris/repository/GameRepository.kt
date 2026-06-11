package com.example.duelotetris.repository

import com.example.duelotetris.network.SocketManager
import kotlinx.coroutines.flow.Flow

class GameRepository(private val socketManager: SocketManager) {

    val events: Flow<SocketManager.SocketEvent> = socketManager.events

    fun connect() = socketManager.connect()

    fun disconnect() = socketManager.disconnect()

    fun createRoom() = socketManager.createRoom()

    fun joinRoom(roomId: String) = socketManager.joinRoom(roomId)

    fun sendAttack(roomId: String, lines: Int) = socketManager.sendAttack(roomId, lines)

    fun gameOver(roomId: String) = socketManager.gameOver(roomId)
}