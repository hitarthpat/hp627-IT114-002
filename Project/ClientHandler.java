import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

public class ClientHandler implements Runnable {
    private Socket clientSocket;
    private HangmanServer server;
    private PrintWriter out;
    private BufferedReader in;
    private Player player;

    public ClientHandler(Socket socket, HangmanServer server) {
        this.clientSocket = socket;
        this.server = server;
    }




    @Override
    public void run() {
        try {
            out = new PrintWriter(clientSocket.getOutputStream(), true);
            in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));

            // First message should be player name
            String playerName = in.readLine();
            player = new Player(playerName, out);
            player.setClientHandler(this);
            server.addPlayer(playerName, player);

            String inputLine;
            while ((inputLine = in.readLine()) != null) {
                handleCommand(inputLine);
            }
        } catch (IOException e) {
            System.err.println("Error handling client: " + e.getMessage());
        } finally {
            if (player != null) {
                server.removePlayer(player.getName());
            }
            try {
                clientSocket.close();
            } catch (IOException e) {
                System.err.println("Error closing socket: " + e.getMessage());
            }
        }
    }

    public void send(String message) {
        out.println(message);
    }

    private void handleCommand(String command) {
        String[] parts = command.split(":", 2);
        String cmd = parts[0];
        String data = parts.length > 1 ? parts[1] : "";

        switch (cmd) {
            case "CREATE_ROOM":
                handleCreateRoom(data);
                break;
            case "JOIN_ROOM":
                handleJoinRoom(data);
                break;
            case "LEAVE_ROOM":
                handleLeaveRoom();
                break;
            case "START_GAME":
                handleStartGame();
                break;
            case "GUESS":
                handleGuess(data);
                break;
            case "LIST_ROOMS":
                handleListRooms();
                break;
            case "SPECTATE":
                handleSpectate(data);
                break;
        }
    }

    private void handleCreateRoom(String data) {
        try {
            String[] parts = data.split(":", 2);
            if (parts.length != 2) {
                player.sendMessage("ERROR:Invalid room creation format");
                return;
            }

            String roomName = parts[0];
            int maxPlayers;
            
            try {
                maxPlayers = Integer.parseInt(parts[1]);
            } catch (NumberFormatException e) {
                player.sendMessage("ERROR:Invalid maximum players number");
                return;
            }

            if (maxPlayers < 2 || maxPlayers > 8) {
                player.sendMessage("ERROR:Maximum players must be between 2 and 8");
                return;
            }

            if (roomName.trim().isEmpty()) {
                player.sendMessage("ERROR:Room name cannot be empty");
                return;
            }

            System.out.println("Creating room: " + roomName + " with max players: " + maxPlayers);
            
            if (server.createRoom(roomName, maxPlayers)) {
                Room room = server.getRoom(roomName);
                if (room.addPlayer(player)) {
                    player.setCurrentRoom(room);
                    player.setSpectator(false);
                    System.out.println("Room created successfully: " + roomName);
                    broadcastRoomState(room);
                    // Notify all clients to refresh their room list
                    for (Player p : server.getPlayers().values()) {
                        handleListRooms();
                    }
                } else {
                    server.removeRoom(roomName);
                    player.sendMessage("ERROR:Failed to join the created room");
                }
            } else {
                player.sendMessage("ERROR:Room already exists");
            }
        } catch (Exception e) {
            System.err.println("Error creating room: " + e.getMessage());
            player.sendMessage("ERROR:Internal server error while creating room");
        }
    }

    private void handleJoinRoom(String roomName) {
        Room room = server.getRoom(roomName);
        if (room != null) {
            if (room.addPlayer(player)) {
                player.setCurrentRoom(room);
                player.setSpectator(false);
                broadcastRoomState(room);
            } else {
                player.sendMessage("ERROR:Room is full or game has started");
            }
        } else {
            player.sendMessage("ERROR:Room does not exist");
        }
    }

    private void handleLeaveRoom() {
        if (player.getCurrentRoom() != null) {
            Room room = player.getCurrentRoom();
            if (player.isSpectator()) {
                room.removeSpectator(player);
            } else {
                room.removePlayer(player);
            }
            player.setCurrentRoom(null);
            broadcastRoomState(room);
        }
    }

    private void handleStartGame() {
        Room room = player.getCurrentRoom();
        if (room != null && room.startGame()) {
            broadcastRoomState(room);
        } else {
            player.sendMessage("ERROR:Cannot start game");
        }
    }

    private void handleGuess(String letter) {
        Room room = player.getCurrentRoom();
        if (room != null && letter.length() == 1) {
            boolean correct = room.makeGuess(player, letter.charAt(0));
            broadcastRoomState(room);
            if (!correct) {
                player.sendMessage("WRONG_GUESS");
            }
        }
    }

    private void handleListRooms() {
        Map<String, Room> rooms = server.getRooms();
        StringBuilder response = new StringBuilder("ROOMS:");
        
        if (rooms.isEmpty()) {
            player.sendMessage("ROOMS:No rooms available");
            return;
        }
        
        for (Room room : rooms.values()) {
            String roomInfo = String.format("%s:%d/%d%s;",
                room.getName(),
                room.getPlayerCount(),
                room.getMaxPlayers(),
                room.isGameStarted() ? " (In Progress)" : ""
            );
            response.append(roomInfo);
        }
        
        player.sendMessage(response.toString());
    }

    private void handleSpectate(String roomName) {
        Room room = server.getRoom(roomName);
        if (room != null) {
            if (room.addSpectator(player)) {
                player.setCurrentRoom(room);
                player.setSpectator(true);
                broadcastRoomState(room);
            } else {
                player.sendMessage("ERROR:Cannot spectate this room");
            }
        } else {
            player.sendMessage("ERROR:Room does not exist");
        }
    }

    private void broadcastRoomState(Room room) {
        // Get the current player name or waiting message
        String currentPlayerInfo;
        if (!room.isGameStarted()) {
            currentPlayerInfo = "Waiting for game to start";
        } else {
            Player currentPlayer = room.getCurrentPlayer();
            currentPlayerInfo = currentPlayer != null ? currentPlayer.getName() : "Game paused";
        }

        String state = "ROOM_STATE:" +
                      room.getName() + ":" +
                      room.getCurrentWordState() + ":" +
                      room.getRemainingAttempts() + ":" +
                      room.isGameStarted() + ":" +
                      room.isGameOver() + ":" +
                      room.isGameWon() + ":" +
                      (room.isGameOver() && !room.isGameWon() ? room.getWord() : "") + ":" +
                      currentPlayerInfo + ":" +
                      formatPlayerGuesses(room);

        // Send to all players in the room
        for (Player p : room.getPlayers()) {
            p.sendMessage(state);
        }
        // Send to all spectators
        for (Player s : room.getSpectators()) {
            s.sendMessage(state);
        }
    }

    
    private String formatPlayerGuesses(Room room) {
        StringBuilder guesses = new StringBuilder();
        for (Player player : room.getPlayers()) {
            Set<Character> playerGuesses = room.getPlayerGuesses(player);
            if (!playerGuesses.isEmpty()) {
                guesses.append(player.getName())
                      .append(":")
                      .append(String.join(",", 
                          playerGuesses.stream()
                                     .map(String::valueOf)
                                     .collect(Collectors.toList())))
                      .append(";");
            }
        }
        return guesses.toString();
    }
} 