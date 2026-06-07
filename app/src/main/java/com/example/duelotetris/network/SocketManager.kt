package com.example.duelotetris.network

import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.receiveAsFlow
import io.socket.client.IO
import io.socket.client.Socket
import org.json.JSONObject

object SocketManager {
    private var socket: Socket? = null

    private val _events = Channel<SocketEvent>(Channel.UNLIMITED)
    val events = _events.receiveAsFlow()

    sealed class SocketEvent {
        data class RoomCreated(val roomId: String) : SocketEvent()
        object GameStart : SocketEvent()
        data class ReceiveAttack(val lines: Int) : SocketEvent()
        object Victory : SocketEvent()
        object OpponentDisconnected : SocketEvent()
        data class Error(val message: String) : SocketEvent()
    }

    fun connect() {
        try {
            val options = IO.Options.builder()
                .setTransports(arrayOf("websocket"))
                .build()

            socket = IO.socket("http://10.62.219.186:3000", options)

            socket?.on(Socket.EVENT_CONNECT) {
                println("Socket connected")
            }

            socket?.on(Socket.EVENT_DISCONNECT) {
                println("Socket disconnected")
            }

            socket?.on(Socket.EVENT_CONNECT_ERROR) { args ->
                println("Connection error: ${args.firstOrNull()}")
                _events.trySend(SocketEvent.Error("Connection failed"))
            }

            socket?.on("room_created") { args ->
                val data = args[0] as? JSONObject
                val roomId = data?.optString("roomId") ?: return@on
                _events.trySend(SocketEvent.RoomCreated(roomId))
            }

            socket?.on("game_start") {
                _events.trySend(SocketEvent.GameStart)
            }

            socket?.on("receive_attack") { args ->
                val data = args[0] as? JSONObject
                val lines = data?.optInt("garbageLines") ?: 0
                _events.trySend(SocketEvent.ReceiveAttack(lines))
            }

            socket?.on("victory") {
                _events.trySend(SocketEvent.Victory)
            }

            socket?.on("opponent_disconnected") {
                _events.trySend(SocketEvent.OpponentDisconnected)
            }

            socket?.on("error_message") { args ->
                val data = args[0] as? JSONObject
                val msg = data?.optString("message") ?: "Unknown error"
                _events.trySend(SocketEvent.Error(msg))
            }

            socket?.connect()
        } catch (e: Exception) {
            e.printStackTrace()
            _events.trySend(SocketEvent.Error("Failed to connect: ${e.message}"))
        }
    }

    fun createRoom() {
        socket?.emit("create_room")
    }

    fun joinRoom(roomId: String) {
        val data = JSONObject()
        data.put("roomId", roomId)
        socket?.emit("join_room", data)
    }

    fun sendAttack(roomId: String, lines: Int) {
        val data = JSONObject()
        data.put("roomId", roomId)
        data.put("garbageLines", lines)
        socket?.emit("send_attack", data)
    }

    fun gameOver(roomId: String) {
        val data = JSONObject()
        data.put("roomId", roomId)
        socket?.emit("game_over", data)
    }

    fun disconnect() {
        socket?.disconnect()
        socket = null
    }
}