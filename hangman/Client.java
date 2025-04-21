/**
 * UCID: [Your UCID]
 * Date: [Current Date]
 * Client class that handles user input and server communication
 */
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Scanner;
import java.util.List;
import java.util.ArrayList;
import javax.swing.SwingUtilities;

public class Client {
    private Socket socket;
    private PrintWriter out;
    private BufferedReader in;
    private String clientName;
    private boolean connected;
    private HangmanUI ui;
    private List<String> userList;
    private String currentRoom;

    public Client() {
        this.connected = false;
        this.userList = new ArrayList<>();
    }

    public boolean connect(String host, int port) {
        if (host == null || host.trim().isEmpty()) {
            System.out.println("Invalid host address");
            return false;
        }
        
        if (port < 1 || port > 65535) {
            System.out.println("Invalid port number");
            return false;
        }

        try {
            socket = new Socket(host, port);
            out = new PrintWriter(socket.getOutputStream(), true);
            in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            connected = true;
            
            // Start thread to listen for server messages
            new Thread(this::listenForMessages).start();
            return true;
        } catch (IOException e) {
            System.out.println("Error connecting to server: " + e.getMessage());
            return false;
        }
    }

    public void setUI(HangmanUI ui) {
        this.ui = ui;
    }

    public void sendMessage(String message) {
        if (!connected || out == null) {
            System.out.println("Not connected to server");
            return;
        }

        if (message == null || message.trim().isEmpty()) {
            System.out.println("Cannot send empty message");
            return;
        }

        out.println(message);
    }

    private void listenForMessages() {
        try {
            String message;
            while (connected && (message = in.readLine()) != null) {
                handleServerMessage(message);
            }
        } catch (IOException e) {
            System.out.println("Error reading from server: " + e.getMessage());
        } finally {
            try {
                if (socket != null) {
                    socket.close();
                }
                connected = false;
                if (ui != null) {
                    ui.showConnectionPanel();
                    ui.getConnectionPanel().showError("Disconnected from server");
                }
            } catch (IOException e) {
                System.out.println("Error closing connection: " + e.getMessage());
            }
        }
    }

    private void handleServerMessage(String message) {
        if (ui == null) {
            System.out.println(message);
            return;
        }

        System.out.println("Received message: " + message);

        if (message.startsWith("DISCONNECT:")) {
            String reason = message.substring(11);
            SwingUtilities.invokeLater(() -> {
                ui.showConnectionPanel();
                ui.getConnectionPanel().showError("Disconnected from server: " + reason);
            });
            disconnect();
        } else if (message.startsWith("ERROR:")) {
            String error = message.substring(6);
            SwingUtilities.invokeLater(() -> ui.getGamePanel().addEvent("Error: " + error));
        } else if (message.startsWith("NAME_SET:")) {
            // After name is set, show room panel
            clientName = message.substring(9).trim();
            System.out.println("Name set, showing room panel"); // Debug line
            SwingUtilities.invokeLater(() -> ui.showRoomPanel());
        } else if (message.startsWith("ROOM_LIST:")) {
            // Handle room list update
            String[] rooms = message.substring(10).split(",");
            for (String room : rooms) {
                if (!room.trim().isEmpty()) {
                    SwingUtilities.invokeLater(() -> ui.getRoomPanel().addRoom(room.trim()));
                }
            }
        } else if (message.startsWith("ROOM_CREATED:")) {
            String roomName = message.substring(13).trim();
            SwingUtilities.invokeLater(() -> ui.getRoomPanel().addRoom(roomName));
        } else if (message.startsWith("ROOM_JOINED:")) {
            String roomName = message.substring(12).trim();
            currentRoom = roomName;
            SwingUtilities.invokeLater(() -> ui.showReadyCheckPanel());
        } else if (message.startsWith("WORD:")) {
            String word = message.substring(5);
            SwingUtilities.invokeLater(() -> ui.getGamePanel().updateWordDisplay(word));
        } else if (message.startsWith("STRIKES:")) {
        int strikes = Integer.parseInt(message.substring(8));
        SwingUtilities.invokeLater(() -> ui.getGamePanel().updateStrikes(strikes));   



        } else if (message.startsWith("TIMER:")) {
            int seconds = Integer.parseInt(message.substring(6));
            SwingUtilities.invokeLater(() -> ui.getGamePanel().updateTimer(seconds));
        } else if (message.startsWith("TURN:")) {
            String playerName = message.substring(5).trim();
            boolean isMyTurn = playerName.equals(clientName);
            SwingUtilities.invokeLater(() -> {
                ui.getGamePanel().resetLetterButtons();
                ui.getGamePanel().setButtonsEnabled(isMyTurn);
            });
        } else if (message.startsWith("LETTER_USED:")) {
            char letter = message.substring(12).charAt(0);
            SwingUtilities.invokeLater(() -> ui.getGamePanel().disableLetter(letter));               
        } else if (message.startsWith("USERS:")) {
            String[] users = message.substring(6).split(",");
            userList.clear();
            for (String user : users) {
                userList.add(user.trim());
            }
            SwingUtilities.invokeLater(() -> ui.getGamePanel().updateUserList(userList));
        } else if (message.startsWith("EVENT:")) {
            String event = message.substring(6);
            SwingUtilities.invokeLater(() -> ui.getGamePanel().addEvent(event));
        } else if (message.equals("GAME_START")) {
            SwingUtilities.invokeLater(() -> {
                ui.showGamePanel();
                ui.getGamePanel().resetLetterButtons();
            });
        } else if (message.equals("GAME_OVER")) {
            SwingUtilities.invokeLater(() -> {
                ui.showReadyCheckPanel();
                ui.getReadyCheckPanel().enableReadyButton();
            });
        } else {
            SwingUtilities.invokeLater(() -> ui.getGamePanel().addEvent(message));
        }
    }

    public String getClientName() {
        return clientName;
    }

    public void setClientName(String name) {
        this.clientName = name;
    }

    public boolean isConnected() {
        return connected;
    }

    public String getCurrentRoom() {
        return currentRoom;
    }

    public void disconnect() {
        if (connected) {
            try {
                if (socket != null) {
                    socket.close();
                }
                connected = false;
            } catch (IOException e) {
                System.out.println("Error disconnecting: " + e.getMessage());
            }
        }
    }
} 