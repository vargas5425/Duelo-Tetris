package com.example.duelotetris.di

import com.example.duelotetris.network.SocketManager
import com.example.duelotetris.repository.GameRepository

class AppContainer {
    private val socketManager = SocketManager()
    val gameRepository: GameRepository = GameRepository(socketManager)
}