/**
 * UCID: [Your UCID]
 * Date: [Current Date]
 * Connection panel for connecting to the server
 */
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class ConnectionPanel extends JPanel {
    private HangmanUI parent;
    private JTextField usernameField;
    private JTextField hostField;
    private JTextField portField;
    private JButton connectButton;
    private JLabel errorLabel;

    public ConnectionPanel(HangmanUI parent) {
        this.parent = parent;
        setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);

        // Username field
        gbc.gridx = 0;
        gbc.gridy = 0;
        add(new JLabel("Username:"), gbc);
        gbc.gridx = 1;
        usernameField = new JTextField(20);
        add(usernameField, gbc);

        // Host field
        gbc.gridx = 0;
        gbc.gridy = 1;
        add(new JLabel("Host:"), gbc);
        gbc.gridx = 1;
        hostField = new JTextField(20);
        hostField.setText("localhost");
        add(hostField, gbc);

        // Port field
        gbc.gridx = 0;
        gbc.gridy = 2;
        add(new JLabel("Port:"), gbc);
        gbc.gridx = 1;
        portField = new JTextField(20);
        portField.setText("8080");
        add(portField, gbc);

        // Error label
        gbc.gridx = 0;
        gbc.gridy = 3;
        gbc.gridwidth = 2;
        errorLabel = new JLabel("");
        errorLabel.setForeground(Color.RED);
        add(errorLabel, gbc);

        // Connect button
        gbc.gridx = 0;
        gbc.gridy = 4;
        gbc.gridwidth = 2;
        connectButton = new JButton("Connect");
        connectButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                connectToServer();
            }
        });
        add(connectButton, gbc);
    }

    private void connectToServer() {
        String username = usernameField.getText().trim();
        String host = hostField.getText().trim();
        String portStr = portField.getText().trim();

        if (username.isEmpty()) {
            showError("Please enter a username");
            return;
        }

        try {
            int port = Integer.parseInt(portStr);
            Client client = new Client();
            client.setUI(parent);  // Set UI before connect
            if (client.connect(host, port)) {
                client.sendMessage("/name " + username);
                client.setClientName(username);
                parent.setClient(client);
                errorLabel.setText("");
            } else {
                showError("Failed to connect to server");
            }
        } catch (NumberFormatException e) {
            showError("Invalid port number");
        } catch (Exception e) {
            showError("Error connecting to server: " + e.getMessage());
        }
    }

    public void showError(String message) {
        errorLabel.setText(message);
        errorLabel.setForeground(Color.RED);
    }
} 