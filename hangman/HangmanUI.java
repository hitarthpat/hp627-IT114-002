package test.hangman;
/**
 * UCID: [Your UCID]
 * Date: [Current Date]
 * Main UI class for the Hangman game
 */
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class HangmanUI extends JFrame {
    private JPanel mainPanel;
    private CardLayout cardLayout;
    private ConnectionPanel connectionPanel;
    private RoomPanel roomPanel;
    private ReadyCheckPanel readyCheckPanel;
    private GamePanel gamePanel;
    private Client client;

    public HangmanUI() {
        setTitle("Hangman Game");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(800, 600);
        setLocationRelativeTo(null);

        // Create main panel with card layout
        mainPanel = new JPanel();
        cardLayout = new CardLayout();
        mainPanel.setLayout(cardLayout);

        // Create and add panels
        connectionPanel = new ConnectionPanel(this);
        roomPanel = new RoomPanel(this);
        readyCheckPanel = new ReadyCheckPanel(this);
        gamePanel = new GamePanel(this);

        mainPanel.add(connectionPanel, "CONNECTION");
        mainPanel.add(roomPanel, "ROOM");
        mainPanel.add(readyCheckPanel, "READY_CHECK");
        mainPanel.add(gamePanel, "GAME");

        add(mainPanel);
        showConnectionPanel();
    }

    public void showConnectionPanel() {
        cardLayout.show(mainPanel, "CONNECTION");
    }

    public void showRoomPanel() {
        cardLayout.show(mainPanel, "ROOM");
    }

    public void showReadyCheckPanel() {
        cardLayout.show(mainPanel, "READY_CHECK");
    }

    public void showGamePanel() {
        cardLayout.show(mainPanel, "GAME");
    }

    public void setClient(Client client) {
        this.client = client;
        client.setUI(this);
    }

    public Client getClient() {
        return client;
    }

    public ConnectionPanel getConnectionPanel() {
        return connectionPanel;
    }

    public RoomPanel getRoomPanel() {
        return roomPanel;
    }

    public ReadyCheckPanel getReadyCheckPanel() {
        return readyCheckPanel;
    }

    public GamePanel getGamePanel() {
        return gamePanel;
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            new HangmanUI().setVisible(true);
        });
    }
} 