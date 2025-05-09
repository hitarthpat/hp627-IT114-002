package test.hangman;
/**
 * UCID: [Your UCID]
 * Date: [Current Date]
 * Main game panel for the Hangman game
 */
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;

public class GamePanel extends JPanel {
    private HangmanUI parent;
    private JPanel userListPanel;
    private JPanel gameAreaPanel;
    private JPanel eventPanel;
    private JTextField wordGuessField;
    private JButton[] letterButtons;
    private JLabel wordDisplayLabel;
    private JLabel timerLabel;
    private JLabel strikesLabel;
    private JTextArea eventLog;
    private List<JLabel> userLabels;
    private JButton skipButton;

    public GamePanel(HangmanUI parent) {
        this.parent = parent;
        setLayout(new BorderLayout());

        // Create panels
        userListPanel = createUserListPanel();
        gameAreaPanel = createGameAreaPanel();
        eventPanel = createEventPanel();

        // Add panels to main panel
        add(userListPanel, BorderLayout.WEST);
        add(gameAreaPanel, BorderLayout.CENTER);
        add(eventPanel, BorderLayout.EAST);
    }

    private JPanel createUserListPanel() {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBorder(BorderFactory.createTitledBorder("Players"));
        
        userLabels = new ArrayList<>();
        return panel;
    }

    private JPanel createGameAreaPanel() {
        JPanel panel = new JPanel();
        panel.setLayout(new BorderLayout());

        // Word display
        wordDisplayLabel = new JLabel("_ _ _ _ _", SwingConstants.CENTER);
        wordDisplayLabel.setFont(new Font("Monospaced", Font.BOLD, 24));
        panel.add(wordDisplayLabel, BorderLayout.NORTH);

        // Letter buttons
        JPanel letterPanel = new JPanel(new GridLayout(4, 7));
        letterButtons = new JButton[26];
        for (char c = 'A'; c <= 'Z'; c++) {
            JButton button = new JButton(String.valueOf(c));
            button.addActionListener(new LetterButtonListener(c));
            letterButtons[c - 'A'] = button;
            letterPanel.add(button);
        }
        panel.add(letterPanel, BorderLayout.CENTER);

        // Word guess input
        JPanel guessPanel = new JPanel();
        wordGuessField = new JTextField(15);
        JButton guessButton = new JButton("Guess Word");
        guessButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                guessWord();
            }
        });
        guessPanel.add(wordGuessField);
        guessPanel.add(guessButton);
        
        skipButton = new JButton("Skip Turn");
        skipButton.addActionListener(e -> {
            Client client = parent.getClient();
            if (client != null && client.isConnected()) {
                client.sendMessage("/skip");
                skipButton.setEnabled(false);  // Prevent repeated skips
    }
});
guessPanel.add(skipButton); 

        panel.add(guessPanel, BorderLayout.SOUTH);

        // Timer and strikes
        JPanel statusPanel = new JPanel();
        timerLabel = new JLabel("Time: 30");
        strikesLabel = new JLabel("Strikes: 0/6");
        statusPanel.add(timerLabel);
        statusPanel.add(strikesLabel);
        panel.add(statusPanel, BorderLayout.NORTH);

        return panel;
    }

    private JPanel createEventPanel() {
        JPanel panel = new JPanel();
        panel.setLayout(new BorderLayout());
        panel.setBorder(BorderFactory.createTitledBorder("Game Events"));

        eventLog = new JTextArea();
        eventLog.setEditable(false);
        JScrollPane scrollPane = new JScrollPane(eventLog);
        panel.add(scrollPane, BorderLayout.CENTER);

        return panel;
    }

    private class LetterButtonListener implements ActionListener {
        private char letter;

        public LetterButtonListener(char letter) {
            this.letter = letter;
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            Client client = parent.getClient();
            if (client != null && client.isConnected()) {
                client.sendMessage("/letter " + letter);
                ((JButton) e.getSource()).setEnabled(false);
            } else {
                JOptionPane.showMessageDialog(GamePanel.this, "Not connected to server", "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void guessWord() {
        String guess = wordGuessField.getText().trim();
        if (guess.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please enter a word to guess", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        if (!guess.matches("[a-zA-Z]+")) {
            JOptionPane.showMessageDialog(this, "Word can only contain letters", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        if (guess.length() > 20) {
            JOptionPane.showMessageDialog(this, "Word is too long (max 20 characters)", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        Client client = parent.getClient();
        if (client != null && client.isConnected()) {
            client.sendMessage("/guess " + guess);
            wordGuessField.setText("");
        } else {
            JOptionPane.showMessageDialog(this, "Not connected to server", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    public void updateWordDisplay(String word) {
        wordDisplayLabel.setText(word);
    }

    public void updateStrikes(int strikes) {
        strikesLabel.setText("Strikes: " + strikes + "/6");
    }

    public void updateTimer(int seconds) {
        timerLabel.setText("Time: " + seconds);
    }

    public void addEvent(String event) {
        eventLog.append(event + "\n");
        eventLog.setCaretPosition(eventLog.getDocument().getLength());
    }

    public void updateUserList(List<String> users) {
        userListPanel.removeAll();
        userLabels.clear();
        
        for (String user : users) {
            JLabel label = new JLabel(user);
            userLabels.add(label);
            userListPanel.add(label);
        }
        
        userListPanel.revalidate();
        userListPanel.repaint();
    }

    public void resetLetterButtons() {
        for (JButton button : letterButtons) {
            button.setEnabled(true);
        }
    }

    public void setButtonsEnabled(boolean enabled) {
        for (JButton button : letterButtons) {
            if (button.isEnabled()) {  // Only enable buttons that haven't been used
                button.setEnabled(enabled);
            }
        }
        wordGuessField.setEnabled(enabled);
        skipButton.setEnabled(enabled);
    }
   
    public void disableLetter(char letter) {
        int index = Character.toUpperCase(letter) - 'A';
        if (index >= 0 && index < letterButtons.length) {
            letterButtons[index].setEnabled(false);
        }
    }
    
} 