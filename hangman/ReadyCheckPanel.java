package test.hangman;
/**
 * UCID: [Your UCID]
 * Date: [Current Date]
 * Ready check panel for game setup
 */
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class ReadyCheckPanel extends JPanel {
    private HangmanUI parent;
    private JButton readyButton;
    private JCheckBox hardModeCheckBox;
    private JCheckBox strikeRemovalCheckBox;
    private JLabel statusLabel;
    private JButton awayButton;
    private JButton spectatorButton;

    public ReadyCheckPanel(HangmanUI parent) {
        this.parent = parent;
        setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);

        // Status label
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 2;
        statusLabel = new JLabel("Waiting for players...");
        add(statusLabel, gbc);

        // Game options
        gbc.gridwidth = 1;
        gbc.gridy = 1;
        hardModeCheckBox = new JCheckBox("Hard Mode");
        add(hardModeCheckBox, gbc);

        gbc.gridy = 2;
        strikeRemovalCheckBox = new JCheckBox("Strike Removal");
        add(strikeRemovalCheckBox, gbc);

        // Ready button
        gbc.gridy = 3;
        gbc.gridwidth = 2;
        readyButton = new JButton("Ready");
        readyButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                markReady();
            }
        });
        add(readyButton, gbc);

        // Away and Spectator buttons
        gbc.gridy = 4;
        gbc.gridwidth = 1;

        JButton awayButton = new JButton("Toggle Away");
        awayButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
            Client client = parent.getClient();
            if (client != null) {
            client.sendMessage("/away");
        }
    }
});
add(awayButton, gbc);

        gbc.gridx = 1;
        JButton spectatorButton = new JButton("Toggle Spectator");
        spectatorButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                Client client = parent.getClient();
                if (client != null) {
                    client.sendMessage("/spectator");
        }
    }
});
add(spectatorButton, gbc);
// Reset gbc for the Ready button (so it spans 2 columns below the new buttons)
gbc.gridx = 0;
gbc.gridy = 4;
gbc.gridwidth = 2;


    }

    private void markReady() {
        Client client = parent.getClient();
        if (client != null) {
            StringBuilder command = new StringBuilder("/ready");
            if (hardModeCheckBox.isSelected()) {
                command.append(" hard");
            }
            if (strikeRemovalCheckBox.isSelected()) {
                command.append(" strike");
            }
            client.sendMessage(command.toString());
            readyButton.setEnabled(false);
            statusLabel.setText("Waiting for other players...");
        }
    }

    public void updateStatus(String message) {
        statusLabel.setText(message);
    }

    public void enableReadyButton() {
        readyButton.setEnabled(true);
    }
} 