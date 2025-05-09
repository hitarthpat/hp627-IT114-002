package test.hangman;
/**
 * UCID: [Your UCID]
 * Date: [Current Date]
 * Room panel for creating and joining game rooms
 */
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class RoomPanel extends JPanel {
    private HangmanUI parent;
    private JTextField roomNameField;
    private JButton createButton;
    private JButton joinButton;
    private JLabel statusLabel;
    private DefaultListModel<String> roomListModel;
    private JList<String> roomList;

    public RoomPanel(HangmanUI parent) {
        this.parent = parent;
        setLayout(new BorderLayout());

        // Create top panel for room creation
        JPanel topPanel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);

        // Room name field
        gbc.gridx = 0;
        gbc.gridy = 0;
        topPanel.add(new JLabel("Room Name:"), gbc);
        gbc.gridx = 1;
        roomNameField = new JTextField(20);
        topPanel.add(roomNameField, gbc);

        // Create button
        gbc.gridx = 2;
        createButton = new JButton("Create Room");
        createButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                createRoom();
            }
        });
        topPanel.add(createButton, gbc);

        // Join button
        gbc.gridx = 3;
        joinButton = new JButton("Join Room");
        joinButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                joinSelectedRoom();
            }
        });
        topPanel.add(joinButton, gbc);

        // Status label
        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.gridwidth = 4;
        statusLabel = new JLabel("");
        statusLabel.setForeground(Color.RED);
        topPanel.add(statusLabel, gbc);

        add(topPanel, BorderLayout.NORTH);

        // Create room list
        roomListModel = new DefaultListModel<>();
        roomList = new JList<>(roomListModel);
        roomList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        JScrollPane scrollPane = new JScrollPane(roomList);
        scrollPane.setBorder(BorderFactory.createTitledBorder("Available Rooms"));
        add(scrollPane, BorderLayout.CENTER);
    }

    private void createRoom() {
        String roomName = roomNameField.getText().trim();
        if (roomName.isEmpty()) {
            showError("Please enter a room name");
            return;
        }

        Client client = parent.getClient();
        if (client == null || !client.isConnected()) {
            showError("Client is not connected");
            return;
        }

        client.sendMessage("/create " + roomName);
        client.sendMessage("/join " + roomName);
        statusLabel.setText("Joining room: " + roomName);
        statusLabel.setForeground(new Color(0, 128, 0)); // Green text for success
        parent.showReadyCheckPanel();
        roomNameField.setText(""); // Clear input
    }

    private void joinSelectedRoom() {
        String selectedRoom = roomList.getSelectedValue();
        if (selectedRoom == null || selectedRoom.trim().isEmpty()) {
            showError("Please select a room to join");
            return;
        }

        selectedRoom = selectedRoom.trim(); // Clean input

        Client client = parent.getClient();
        if (client == null || !client.isConnected()) {
            showError("Client is not connected");
            return;
        }

        client.sendMessage("/join " + selectedRoom);
        statusLabel.setText("Joining room: " + selectedRoom);
        statusLabel.setForeground(new Color(0, 128, 0));
        parent.showReadyCheckPanel();
    }

    public void addRoom(String roomName) {
        if (!roomListModel.contains(roomName)) {
            roomListModel.addElement(roomName);
        }
    }

    public void removeRoom(String roomName) {
        roomListModel.removeElement(roomName);
    }

    public void showError(String message) {
        statusLabel.setText(message);
        statusLabel.setForeground(Color.RED);
    }
}