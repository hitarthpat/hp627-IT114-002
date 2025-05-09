import javax.swing.*;
import javax.swing.border.TitledBorder;
import javax.swing.border.EtchedBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.*;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

public class HangmanClient extends JFrame {
    private Socket socket;
    private PrintWriter out;
    private BufferedReader in;
    private String playerName;
    private JPanel mainPanel;
    private JPanel roomPanel;
    private JPanel gamePanel;
    private JList<String> roomList;
    private DefaultListModel<String> roomListModel;
    private JTextField roomNameField;
    private JTextField maxPlayersField;
    private JTextField guessField;
    private JLabel wordLabel;
    private JLabel attemptsLabel;
    private JLabel statusLabel;
    private JLabel currentPlayerLabel;
    private JButton createRoomButton;
    private JButton joinRoomButton;
    private JButton spectateButton;
    private JButton startGameButton;
    private JButton guessButton;
    private JButton leaveRoomButton;
    private Timer roomListTimer;

    public HangmanClient() {
        setupUI();
        connectToServer();
    }

    private void setupUI() {
        setTitle("Hangman Game");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(600, 400);
        setLayout(new BorderLayout());

        // Main panel with card layout
        mainPanel = new JPanel(new CardLayout());
        
        // Room selection panel
        roomPanel = new JPanel(new BorderLayout());
        setupRoomPanel();
        
        // Game panel
        gamePanel = new JPanel(new BorderLayout());
        setupGamePanel();
        
        // Add panels to main panel
        mainPanel.add(roomPanel, "ROOM");
        mainPanel.add(gamePanel, "GAME");
        
        add(mainPanel, BorderLayout.CENTER);
        
        // Show room panel initially
        showRoomPanel();
    }

    private void setupRoomPanel() {
        roomPanel.setLayout(new BorderLayout(10, 10));
        roomPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // Top panel for room creation
        JPanel topPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 5));
        topPanel.setBorder(BorderFactory.createTitledBorder(
            BorderFactory.createEtchedBorder(), 
            "Create New Room",
            TitledBorder.CENTER,
            TitledBorder.TOP,
            new Font("Arial", Font.BOLD, 14)
        ));

        // Room name input with label
        JPanel namePanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JLabel nameLabel = new JLabel("Room Name:");
        nameLabel.setFont(new Font("Arial", Font.BOLD, 12));
        roomNameField = new JTextField(15);
        roomNameField.setFont(new Font("Arial", Font.PLAIN, 12));
        namePanel.add(nameLabel);
        namePanel.add(roomNameField);

        // Max players input with label
        JPanel playersPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JLabel playersLabel = new JLabel("Max Players:");
        playersLabel.setFont(new Font("Arial", Font.BOLD, 12));
        maxPlayersField = new JTextField(5);
        maxPlayersField.setFont(new Font("Arial", Font.PLAIN, 12));
        maxPlayersField.setText("4"); // Default value
        playersPanel.add(playersLabel);
        playersPanel.add(maxPlayersField);

        // Create room button with styling
        createRoomButton = new JButton("Create Room");
        createRoomButton.setFont(new Font("Arial", Font.BOLD, 12));
        createRoomButton.setBackground(new Color(70, 150, 70));
        createRoomButton.setForeground(Color.WHITE);
        createRoomButton.setFocusPainted(false);

        topPanel.add(namePanel);
        topPanel.add(playersPanel);
        topPanel.add(createRoomButton);

        // Center panel for room list
        JPanel centerPanel = new JPanel(new BorderLayout(5, 5));
        centerPanel.setBorder(BorderFactory.createTitledBorder(
            BorderFactory.createEtchedBorder(),
            "Available Rooms",
            TitledBorder.CENTER,
            TitledBorder.TOP,
            new Font("Arial", Font.BOLD, 14)
        ));

        // Room list with custom renderer
        roomListModel = new DefaultListModel<>();
        roomList = new JList<>(roomListModel);
        roomList.setFont(new Font("Arial", Font.PLAIN, 12));
        roomList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        roomList.setCellRenderer(new DefaultListCellRenderer() {
            @Override
            public Component getListCellRendererComponent(JList<?> list, Object value, 
                    int index, boolean isSelected, boolean cellHasFocus) {
                JLabel label = (JLabel) super.getListCellRendererComponent(
                    list, value, index, isSelected, cellHasFocus);
                
                try {
                    String roomInfo = value.toString();
                    String[] parts = roomInfo.split(":");
                    if (parts.length >= 2) {
                        String roomName = parts[0];
                        String playerCount = parts[1];
                        label.setText(String.format("<html><b>%s</b> - Players: %s</html>", 
                            roomName, playerCount));
                    } else {
                        label.setText(roomInfo);
                    }
                } catch (Exception e) {
                    label.setText(value.toString());
                }
                
                if (isSelected) {
                    label.setBackground(new Color(51, 153, 255));
                }
                
                return label;
            }
        });

        JScrollPane scrollPane = new JScrollPane(roomList);
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
        centerPanel.add(scrollPane, BorderLayout.CENTER);

        // Bottom panel for actions
        JPanel bottomPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 5));
        bottomPanel.setBorder(BorderFactory.createEmptyBorder(5, 0, 5, 0));

        joinRoomButton = new JButton("Join Room");
        spectateButton = new JButton("Spectate");
        
        for (JButton button : new JButton[]{joinRoomButton, spectateButton}) {
            button.setFont(new Font("Arial", Font.BOLD, 12));
            button.setFocusPainted(false);
        }

        joinRoomButton.setBackground(new Color(51, 153, 255));
        joinRoomButton.setForeground(Color.WHITE);
        
        spectateButton.setBackground(new Color(150, 150, 150));
        spectateButton.setForeground(Color.WHITE);

        bottomPanel.add(joinRoomButton);
        bottomPanel.add(spectateButton);

        roomPanel.add(topPanel, BorderLayout.NORTH);
        roomPanel.add(centerPanel, BorderLayout.CENTER);
        roomPanel.add(bottomPanel, BorderLayout.SOUTH);

        // Add action listeners
        createRoomButton.addActionListener(e -> createRoom());
        joinRoomButton.addActionListener(e -> joinRoom(false));
        spectateButton.addActionListener(e -> joinRoom(true));

        // Add key listeners
        roomNameField.addActionListener(e -> maxPlayersField.requestFocus());
        maxPlayersField.addActionListener(e -> createRoom());

        // Enable/disable join buttons based on selection
        roomList.addListSelectionListener(e -> {
            boolean hasSelection = roomList.getSelectedValue() != null;
            joinRoomButton.setEnabled(hasSelection);
            spectateButton.setEnabled(hasSelection);
        });

        // Initially disable join buttons
        joinRoomButton.setEnabled(false);
        spectateButton.setEnabled(false);

        // Start room list refresh timer
        Timer refreshTimer = new Timer(2000, e -> refreshRoomList());
        refreshTimer.start();
    }

    private void setupGamePanel() {
        // Main game panel with better layout
        gamePanel.setLayout(new BorderLayout(10, 10));
        gamePanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // Top panel for game info with better styling
        JPanel topPanel = new JPanel(new GridLayout(2, 1, 5, 5));
        topPanel.setBorder(BorderFactory.createTitledBorder("Game Status"));
        
        // Word display with better styling
        wordLabel = new JLabel("", SwingConstants.CENTER);
        wordLabel.setFont(new Font("Arial", Font.BOLD, 32));
        wordLabel.setForeground(Color.BLUE);
        
        // Current player display with better styling
        currentPlayerLabel = new JLabel("", SwingConstants.CENTER);
        currentPlayerLabel.setFont(new Font("Arial", Font.BOLD, 16));
        currentPlayerLabel.setForeground(Color.RED);
        
        topPanel.add(wordLabel);
        topPanel.add(currentPlayerLabel);
        
        gamePanel.add(topPanel, BorderLayout.NORTH);

        // Center panel for status with better layout
        JPanel centerPanel = new JPanel(new BorderLayout(5, 5));
        centerPanel.setBorder(BorderFactory.createTitledBorder("Game Information"));
        
        // Attempts display
        JPanel attemptsPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        attemptsLabel = new JLabel("", SwingConstants.CENTER);
        attemptsLabel.setFont(new Font("Arial", Font.BOLD, 14));
        attemptsPanel.add(attemptsLabel);
        
        // Status display with scroll pane
        JPanel statusPanel = new JPanel(new BorderLayout());
        statusLabel = new JLabel("", SwingConstants.LEFT);
        statusLabel.setFont(new Font("Arial", Font.PLAIN, 12));
        JScrollPane scrollPane = new JScrollPane(statusLabel);
        scrollPane.setPreferredSize(new Dimension(300, 150));
        statusPanel.add(scrollPane, BorderLayout.CENTER);
        
        centerPanel.add(attemptsPanel, BorderLayout.NORTH);
        centerPanel.add(statusPanel, BorderLayout.CENTER);
        
        gamePanel.add(centerPanel, BorderLayout.CENTER);

        // Bottom panel for input with better layout
        JPanel bottomPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 10));
        bottomPanel.setBorder(BorderFactory.createTitledBorder("Game Controls"));
        
        // Input field with better styling
        guessField = new JTextField(2);
        guessField.setFont(new Font("Arial", Font.BOLD, 16));
        guessField.setHorizontalAlignment(JTextField.CENTER);
        
        // Buttons with better styling
        guessButton = new JButton("Guess");
        startGameButton = new JButton("Start Game");
        leaveRoomButton = new JButton("Leave Room");
        
        // Style buttons
        for (JButton button : new JButton[]{guessButton, startGameButton, leaveRoomButton}) {
            button.setFont(new Font("Arial", Font.BOLD, 12));
            button.setBackground(new Color(220, 220, 220));
            button.setFocusPainted(false);
        }
        
        bottomPanel.add(new JLabel("Guess a letter:"));
        bottomPanel.add(guessField);
        bottomPanel.add(guessButton);
        bottomPanel.add(startGameButton);
        bottomPanel.add(leaveRoomButton);
        
        gamePanel.add(bottomPanel, BorderLayout.SOUTH);
        
        // Add action listeners
        guessButton.addActionListener(e -> makeGuess());
        startGameButton.addActionListener(e -> startGame());
        leaveRoomButton.addActionListener(e -> leaveRoom());
    }

    private void connectToServer() {
        try {
            socket = new Socket("localhost", 1234);
            out = new PrintWriter(socket.getOutputStream(), true);
            in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            
            // Get player name
            playerName = JOptionPane.showInputDialog(this, "Enter your name:");
            if (playerName == null || playerName.trim().isEmpty()) {
                playerName = "Player" + System.currentTimeMillis();
            }
            out.println(playerName);
            
            // Start listening for server messages
            new Thread(this::listenForServerMessages).start();
            
            // Start room list refresh timer
            roomListTimer = new Timer(2000, e -> refreshRoomList());
            roomListTimer.start();
        } catch (IOException e) {
            JOptionPane.showMessageDialog(this, 
                "Could not connect to server: " + e.getMessage());
            System.exit(1);
        }
    }

    private void createRoom() {
        String roomName = roomNameField.getText().trim();
        String maxPlayersStr = maxPlayersField.getText().trim();
        
        if (roomName.isEmpty()) {
            showError("Please enter a room name");
            roomNameField.requestFocus();
            return;
        }
        
        try {
            int maxPlayers = Integer.parseInt(maxPlayersStr);
            if (maxPlayers < 2) {
                showError("Maximum players must be at least 2");
                maxPlayersField.requestFocus();
                return;
            }
            if (maxPlayers > 8) {
                showError("Maximum players cannot exceed 8");
                maxPlayersField.requestFocus();
                return;
            }
            
            out.println("CREATE_ROOM:" + roomName + ":" + maxPlayers);
            roomNameField.setText("");
            maxPlayersField.setText("4");
        } catch (NumberFormatException e) {
            showError("Please enter a valid number for maximum players");
            maxPlayersField.requestFocus();
        }
    }

    private void joinRoom(boolean spectate) {
        String selectedRoom = roomList.getSelectedValue();
        if (selectedRoom == null) {
            showError("Please select a room");
            return;
        }
        
        try {
            String roomName = selectedRoom.split(":")[0];
            if (spectate) {
                out.println("SPECTATE:" + roomName);
            } else {
                out.println("JOIN_ROOM:" + roomName);
            }
        } catch (Exception e) {
            showError("Invalid room selection");
        }
    }

    private void leaveRoom() {
        out.println("LEAVE_ROOM");
        showRoomPanel();
    }

    private void startGame() {
        out.println("START_GAME");
    }

    private void makeGuess() {
        String guess = guessField.getText().toUpperCase();
        if (guess.length() == 1 && Character.isLetter(guess.charAt(0))) {
            out.println("GUESS:" + guess);
            guessField.setText("");
        } else {
            JOptionPane.showMessageDialog(this, "Please enter a single letter");
        }
    }

    private void refreshRoomList() {
        out.println("LIST_ROOMS");
    }

    private void showRoomPanel() {
        ((CardLayout) mainPanel.getLayout()).show(mainPanel, "ROOM");
    }

    private void showGamePanel() {
        ((CardLayout) mainPanel.getLayout()).show(mainPanel, "GAME");
    }

    private void listenForServerMessages() {
        try {
            String line;
            while ((line = in.readLine()) != null) {
                handleServerMessage(line);
            }
        } catch (IOException e) {
            JOptionPane.showMessageDialog(this, 
                "Connection to server lost: " + e.getMessage());
            System.exit(1);
        }
    }

    private void handleServerMessage(String message) {
        String[] parts = message.split(":", 2);
        String cmd = parts[0];
        String data = parts.length > 1 ? parts[1] : "";

        switch (cmd) {
            case "ROOMS":
                updateRoomList(data);
                break;
            case "ROOM_STATE":
                updateGameState(data);
                break;
            case "ERROR":
                showError(data);
                // Re-enable buttons on error
                if (data.contains("full") || data.contains("already in")) {
                    SwingUtilities.invokeLater(() -> {
                        joinRoomButton.setEnabled(true);
                        spectateButton.setEnabled(true);
                    });
                }
                break;
        }
    }

    private void updateRoomList(String data) {
        SwingUtilities.invokeLater(() -> {
            // Store the currently selected room
            String selectedRoom = roomList.getSelectedValue();
            
            roomListModel.clear();
            String[] rooms = data.split(";");
            for (String room : rooms) {
                if (!room.isEmpty() && !room.equals("No rooms available")) {
                    roomListModel.addElement(room);
                }
            }
            
            // Restore the selection if the room still exists
            if (selectedRoom != null) {
                for (int i = 0; i < roomListModel.size(); i++) {
                    String room = roomListModel.getElementAt(i);
                    if (room.startsWith(selectedRoom.split(":")[0] + ":")) {
                        roomList.setSelectedIndex(i);
                        break;
                    }
                }
            }
            
            // Update button states
            boolean hasRooms = roomListModel.size() > 0;
            if (!hasRooms) {
                joinRoomButton.setEnabled(false);
                spectateButton.setEnabled(false);
            }
        });
    }

    private void updateGameState(String data) {
        String[] parts = data.split(":");
        String roomName = parts[0];
        String wordState = parts[1];
        int attempts = Integer.parseInt(parts[2]);
        boolean gameStarted = Boolean.parseBoolean(parts[3]);
        boolean gameOver = Boolean.parseBoolean(parts[4]);
        boolean gameWon = Boolean.parseBoolean(parts[5]);
        String secretWord = parts[6];
        String currentPlayer = parts[7];
        String guessesInfo = parts.length > 8 ? parts[8] : "";

        SwingUtilities.invokeLater(() -> {
            // Format word state with spaces between letters
            StringBuilder formattedWord = new StringBuilder();
            for (char c : wordState.toCharArray()) {
                formattedWord.append(c).append(" ");
            }
            wordLabel.setText(formattedWord.toString().trim());
            
            // Update attempts with visual indicator
            attemptsLabel.setText(String.format("Remaining attempts: %d %s", 
                attempts, 
                "❤".repeat(attempts)));
            
            // Update current player and turn information with better formatting
            String turnInfo = String.format("<html><div style='text-align: center;'>" +
                "<span style='color: %s; font-weight: bold;'>%s's turn</span>%s</div></html>",
                currentPlayer.equals(playerName) ? "#FF0000" : "#000000",
                currentPlayer,
                currentPlayer.equals(playerName) ? " (Your turn!)" : "");
            currentPlayerLabel.setText(turnInfo);
            
            // Update status with player guesses in a better format
            StringBuilder statusText = new StringBuilder("<html><div style='margin: 5px;'>");
            
            // Add game status
            if (gameStarted) {
                statusText.append("<div style='margin-bottom: 10px;'><b>Game in Progress</b></div>");
            }
            
            // Add player guesses
            if (!guessesInfo.isEmpty()) {
                String[] playerGuesses = guessesInfo.split(";");
                for (String playerGuess : playerGuesses) {
                    if (!playerGuess.isEmpty()) {
                        String[] guessData = playerGuess.split(":");
                        String playerName = guessData[0];
                        String guesses = guessData.length > 1 ? guessData[1] : "";
                        statusText.append(String.format(
                            "<div style='margin: 2px;'><b>%s</b> guessed: <span style='color: #0000FF;'>%s</span></div>",
                            playerName,
                            guesses.isEmpty() ? "No guesses yet" : guesses.replace(",", ", ")));
                    }
                }
            }
            
            if (gameOver) {
                statusText.append("<div style='margin-top: 10px; color: ");
                if (gameWon) {
                    statusText.append("#00AA00'><b>Game Over - ").append(currentPlayer).append(" won!</b>");
                } else {
                    statusText.append("#AA0000'><b>Game Over - The word was: ").append(secretWord).append("</b>");
                }
                statusText.append("</div>");
            }
            
            statusText.append("</div></html>");
            statusLabel.setText(statusText.toString());
            
            // Update button states
            if (gameStarted) {
                guessButton.setEnabled(currentPlayer.equals(playerName));
                startGameButton.setEnabled(false);
            } else {
                guessButton.setEnabled(false);
                startGameButton.setEnabled(true);
            }
            
            if (gameOver) {
                guessButton.setEnabled(false);
                startGameButton.setEnabled(false);
            }
            
            showGamePanel();
        });
    }

    private void showError(String message) {
        JOptionPane.showMessageDialog(this,
            message,
            "Error",
            JOptionPane.ERROR_MESSAGE);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new HangmanClient().setVisible(true));
    }
} 