const express = require("express");
const http = require("http");
const { Server } = require("socket.io");
const cors = require("cors");

const app = express();
app.use(cors());

const server = http.createServer(app);
const io = new Server(server, {
    cors: { origin: "*" }
});

const rooms = {};

io.on("connection", (socket) => {
    console.log("Conectado:", socket.id);

    socket.on("create_room", () => {
        const roomId = Math.random().toString(36).substring(2, 8).toUpperCase();
        rooms[roomId] = { players: [socket.id] };
        socket.join(roomId);
        socket.emit("room_created", { roomId });
        console.log(`Sala creada: ${roomId}`);
    });

    socket.on("join_room", ({ roomId }) => {
        const room = rooms[roomId];
        if (!room) {
            socket.emit("error_message", { message: "Sala no encontrada" });
            return;
        }
        if (room.players.length >= 2) {
            socket.emit("error_message", { message: "Sala llena" });
            return;
        }
        room.players.push(socket.id);
        socket.join(roomId);

        console.log(`Jugador ${socket.id} unido a ${roomId}`);
        io.to(roomId).emit("game_start");
    });

    socket.on("send_attack", ({ roomId, garbageLines }) => {
        socket.to(roomId).emit("receive_attack", { garbageLines });
    });

    socket.on("game_over", ({ roomId }) => {
        console.log(`=== GAME OVER en sala ${roomId} ===`);

        const room = rooms[roomId];
        if (room) {

            const otherPlayer = room.players.find(id => id !== socket.id);
            console.log(`Enviando victory a: ${otherPlayer}`);

            // victory
            io.to(otherPlayer).emit("victory");

            // Eliminar la sala
            delete rooms[roomId];
            console.log(`Sala ${roomId} eliminada`);
        } else {
            console.log(`ERROR: Sala ${roomId} no encontrada`);
        }
    });

    socket.on("disconnect", () => {
        console.log("Desconectado:", socket.id);

        for (const roomId in rooms) {
            const room = rooms[roomId];
            if (room.players.includes(socket.id)) {
                const otherPlayer = room.players.find(id => id !== socket.id);
                if (otherPlayer) {
                    io.to(otherPlayer).emit("opponent_disconnected");
                }
                delete rooms[roomId];
                console.log(`Sala ${roomId} eliminada por desconexión`);
            }
        }
    });
});

app.get("/", (_, res) => res.send("Tetris Duel Server Running"));

server.listen(3000, "0.0.0.0", () => {
    console.log("Servidor corriendo en http://localhost:3000");
});