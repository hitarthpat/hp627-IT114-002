/**
 * UCID: [Your UCID]
 * Date: [Current Date]
 * Server class that handles client connections and manages rooms
 */
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashMap;
import java.util.Map;

public class Server {
    private ServerSocket serverSocket;
    private Map<String, Room> rooms;
    private boolean running;

    public Server(int port) {
        try {
            serverSocket = new ServerSocket(port);
            rooms = new HashMap<>();
            rooms.put("Lobby", new Room("Lobby"));
            running = true;
            System.out.println("Server started on port " + port);
        } catch (Exception e) {
            System.out.println("Error starting server: " + e.getMessage());
        }
    }

    public void start() {
        try {
            while (running) {
                Socket clientSocket = serverSocket.accept();
                System.out.println("New client connected");
                ServerThread clientThread = new ServerThread(clientSocket, this);
                clientThread.start();
            }
        } catch (Exception e) {
            System.out.println("Server error: " + e.getMessage());
        }
    }

    public Room getRoom(String roomName) {
        return rooms.get(roomName);
    }

    public Map<String, Room> getRooms() {
        return rooms;
    }

    public void createGameRoom(String roomName) {
        if (!rooms.containsKey(roomName)) {
            rooms.put(roomName, new GameRoom(roomName));
        }
    }

    public void createRoom(String roomName) {
        if (!rooms.containsKey(roomName)) {
            rooms.put(roomName, new Room(roomName));
        }
    }

    public static void main(String[] args) {
        if (args.length != 1) {
            System.out.println("Usage: java Server <port>");
            return;
        }
        
        int port = Integer.parseInt(args[0]);
        Server server = new Server(port);
        server.start();
    }
} 