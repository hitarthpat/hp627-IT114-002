/**
 * UCID: [Your UCID]
 * Date: [Current Date]
 * ServerThread class that handles individual client connections and message processing
 */
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.UUID;

public class ServerThread extends Thread {
    private Socket socket;
    private Server server;
    private PrintWriter out;
    private BufferedReader in;
    private String clientName;
    private Room currentRoom;
    private String clientId;
    private Player player;
    private boolean isReady;
    private boolean isAway;
    private boolean isSpectator;

    public ServerThread(Socket socket, Server server) {
        this.socket = socket;
        this.server = server;
        this.clientId = UUID.randomUUID().toString();
        this.isReady = false;
        this.isAway = false;
        this.isSpectator = false;
        try {
            out = new PrintWriter(socket.getOutputStream(), true);
            in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        } catch (IOException e) {
            System.out.println("Error creating server thread: " + e.getMessage());
        }
    }

    public void run() {
        try {
            String inputLine;
            while ((inputLine = in.readLine()) != null) {
                handleCommand(inputLine);
            }
        } catch (IOException e) {
            System.out.println("Error in server thread: " + e.getMessage());
        } finally {
            try {
                socket.close();
                if (currentRoom != null) {
                    currentRoom.removeClient(this);
                }
            } catch (IOException e) {
                System.out.println("Error closing socket: " + e.getMessage());
            }
        }
    }

    private void handleCommand(String command) {
        if (command.startsWith("/name ")) {
            clientName = command.substring(6);
            player = new Player(clientId, clientName);
            System.out.println("Setting name for client: " + clientName);
            sendMessage("NAME_SET:" + clientName);
            
            // Send room list after name is set
            StringBuilder roomList = new StringBuilder("ROOM_LIST:");
            for (String roomName : server.getRooms().keySet()) {
                if (!roomName.equals("Lobby")) {
                    roomList.append(roomName).append(",");
                }
            }
            System.out.println("Sending room list: " + roomList.toString());
            sendMessage(roomList.toString());
            
            // Join lobby
            Room lobby = server.getRoom("Lobby");
            if (lobby != null) {
                currentRoom = lobby;
                currentRoom.addClient(this);
            }
        } else if (command.startsWith("/connect")) {
            currentRoom.addClient(this);
            sendMessage("Connected to room: " + currentRoom.getName());
        } else if (command.startsWith("/create ")) {
            String roomName = command.substring(8);
            server.createGameRoom(roomName);  // Create a game room specifically
            System.out.println("Created game room: " + roomName);
            sendMessage("ROOM_CREATED:" + roomName);
            // Broadcast updated room list to all clients
            broadcastRoomList();
        } else if (command.startsWith("/join ")) {
            String roomName = command.substring(6);
            Room newRoom = server.getRoom(roomName);
            if (newRoom != null) {
                if (currentRoom != null) {
                    currentRoom.removeClient(this);
                }
                currentRoom = newRoom;
                currentRoom.addClient(this);
                if (newRoom instanceof GameRoom) {
                    ((GameRoom) newRoom).handlePlayerReconnect(this);
                }
                if (newRoom instanceof GameRoom) {
                    System.out.println("Client " + clientName + " joined game room: " + roomName);
                    sendMessage("ROOM_JOINED:" + roomName);
                }
            } else {
                sendMessage("Room not found: " + roomName);
            }
        } else if (command.startsWith("/guess ") || command.startsWith("/letter ")) {
            if (currentRoom instanceof GameRoom) {
                ((GameRoom) currentRoom).handleGuess(command.substring(command.indexOf(' ') + 1), this);
            } else {
                sendMessage("This room is not a game room!");
            }
        } else if (command.equals("/skip")) {
            if (currentRoom instanceof GameRoom) {
                ((GameRoom) currentRoom).skipTurn(this);
            }        
        } else if (command.startsWith("/ready")) {
            isReady = true;
            if (currentRoom instanceof GameRoom) {
                GameRoom gameRoom = (GameRoom) currentRoom;
                boolean hardMode = command.contains("hard");
                boolean strikeRemoval = command.contains("strike");
                gameRoom.handleReady(this, hardMode, strikeRemoval);
            }
        } else if (command.equals("/away")) {
            isAway = !isAway;
            currentRoom.broadcastMessage(clientName + (isAway ? " is now away" : " is no longer away"));
        } else if (command.equals("/spectator")) {
            isSpectator = !isSpectator;
            currentRoom.broadcastMessage(clientName + (isSpectator ? " joined as spectator" : " is no longer a spectator"));
        } else {
            // Regular message
            currentRoom.broadcastMessage(clientName + ": " + command);
        }
    }

    private void broadcastRoomList() {
        StringBuilder roomList = new StringBuilder("ROOM_LIST:");
        for (String roomName : server.getRooms().keySet()) {
            if (!roomName.equals("Lobby")) {
                roomList.append(roomName).append(",");
            }
        }
        String message = roomList.toString();
        System.out.println("Broadcasting room list: " + message);
        for (Room room : server.getRooms().values()) {
            for (ServerThread client : room.getClients()) {
                client.sendMessage(message);
            }
        }
    }

    public void sendMessage(String message) {
        out.println(message);
    }

    public String getClientName() {
        return clientName;
    }

    public String getClientId() {
        return clientId;
    }

    public Player getPlayer() {
        return player;
    }

    public boolean isReady() {
        return isReady;
    }

    public void setReady(boolean ready) {
        isReady = ready;
    }

    public boolean isAway() {
        return isAway;
    }

    public boolean isSpectator() {
        return isSpectator;
    }
} 