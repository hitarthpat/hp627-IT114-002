package test.hangman;
/**
 * UCID: [Your UCID]
 * Date: [Current Date]
 * Room class that manages clients and their interactions within a room
 */
import java.util.ArrayList;
import java.util.List;

public class Room {
    private String name;
    private List<ServerThread> clients;

    public Room(String name) {
        this.name = name;
        this.clients = new ArrayList<>();
    }

    public String getName() {
        return name;
    }

    public void addClient(ServerThread client) {
        clients.add(client);
        broadcastMessage(client.getClientName() + " joined the room");
    }

    public void removeClient(ServerThread client) {
        clients.remove(client);
        broadcastMessage(client.getClientName() + " left the room");
    }

    public void broadcastMessage(String message) {
        for (ServerThread client : clients) {
            client.sendMessage(message);
        }
    }

    public List<ServerThread> getClients() {
        return clients;
    }
} 