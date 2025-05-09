import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class HangmanServer {
    private static final int PORT = 1234;
    private Map<String, Room> rooms;
    private Map<String, Player> players;

    public HangmanServer() {
        rooms = new ConcurrentHashMap<>();
        players = new ConcurrentHashMap<>();
    }

    public void start() {
        try (ServerSocket serverSocket = new ServerSocket(PORT)) {
            System.out.println("Hangman Server started on port " + PORT);
            
            while (true) {
                Socket clientSocket = serverSocket.accept();
                System.out.println("New client connected: " + clientSocket.getInetAddress());
                
                new Thread(new ClientHandler(clientSocket, this)).start();
            }
        } catch (IOException e) {
            System.err.println("Server error: " + e.getMessage());
        }
    }

    public synchronized boolean createRoom(String roomName, int maxPlayers) {
        if (!rooms.containsKey(roomName)) {
            System.out.println("Creating new room: " + roomName + " with max players: " + maxPlayers);
            rooms.put(roomName, new Room(roomName, maxPlayers));
            return true;
        }
        System.out.println("Room already exists: " + roomName);
        return false;
    }

    public synchronized Room getRoom(String roomName) {
        return rooms.get(roomName);
    }

    public synchronized Map<String, Room> getRooms() {
        return new HashMap<>(rooms);
    }

    public synchronized void removeRoom(String roomName) {
        Room room = rooms.remove(roomName);
        if (room != null) {
            System.out.println("Removed room: " + roomName);
            // Update all players that were in this room
            for (Player player : room.getPlayers()) {
                player.setCurrentRoom(null);
                player.sendMessage("ROOM_CLOSED:" + roomName);
            }
            for (Player spectator : room.getSpectators()) {
                spectator.setCurrentRoom(null);
                spectator.sendMessage("ROOM_CLOSED:" + roomName);
            }
        }
    }

    public synchronized void addPlayer(String playerName, Player player) {
        players.put(playerName, player);
    }

    public synchronized Player getPlayer(String playerName) {
        return players.get(playerName);
    }

    public synchronized void removePlayer(String playerName) {
        Player player = players.remove(playerName);
        if (player != null && player.getCurrentRoom() != null) {
            Room room = player.getCurrentRoom();
            if (player.isSpectator()) {
                room.removeSpectator(player);
            } else {
                room.removePlayer(player);
            }
            if (room.getPlayerCount() == 0) {
                removeRoom(room.getName());
            }
        }
    }

    public synchronized Map<String, Player> getPlayers() {
        return new HashMap<>(players);
    }

    public static void main(String[] args) {
        new HangmanServer().start();
    }
} 