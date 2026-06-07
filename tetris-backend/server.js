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
        io.to(roomId).emit("game_start");
        console.log(`Jugador unido a ${roomId}`);
    });

    socket.on("send_attack", ({ roomId, garbageLines }) => {
        console.log(`Ataque: ${garbageLines} lineas -> sala ${roomId}`);
        socket.to(roomId).emit("receive_attack", { garbageLines });
    });

    socket.on("game_over", ({ roomId }) => {
        console.log(`Game over en sala ${roomId}`);
        socket.to(roomId).emit("victory");
    });

    socket.on("disconnect", () => {
        for (const roomId in rooms) {
            const room = rooms[roomId];
            if (room.players.includes(socket.id)) {
                socket.to(roomId).emit("opponent_disconnected");
                delete rooms[roomId];
                console.log(`Desconectado: ${socket.id}, sala ${roomId} eliminada`);
            }
        }
    });
});

app.get("/", (_, res) => res.send("Tetris Duel Server Running"));

server.listen(3000, "0.0.0.0", () => {
    console.log("Servidor corriendo en http://localhost:3000");
});