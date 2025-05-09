import java.io.PrintWriter;

public class Player {
    private String name;
    private PrintWriter out;
    private Room currentRoom;
    private boolean isSpectator;
    private ClientHandler clientHandler;

    public Player(String name, PrintWriter out) {
        this.name = name;
        this.out = out;
        this.currentRoom = null;
        this.isSpectator = false;
    }

    public void setClientHandler(ClientHandler handler) {
        this.clientHandler = handler;
    }

    public ClientHandler getClientHandler() {
        return clientHandler;
    }

    public String getName() {
        return name;
    }

    public PrintWriter getWriter() {
        return out;
    }

    public Room getCurrentRoom() {
        return currentRoom;
    }

    public void setCurrentRoom(Room room) {
        this.currentRoom = room;
    }

    public boolean isSpectator() {
        return isSpectator;
    }

    public void setSpectator(boolean spectator) {
        isSpectator = spectator;
    }

    public void sendMessage(String message) {
        out.println(message);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Player player = (Player) o;
        return name.equals(player.name);
    }

    @Override
    public int hashCode() {
        return name.hashCode();
    }
} 