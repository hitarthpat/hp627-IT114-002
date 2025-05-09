import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class HangmanUI extends JFrame {
    private HangmanGame game;
    private JLabel wordLabel;
    private JLabel attemptsLabel;
    private JLabel statusLabel;
    private JTextField guessField;
    private JButton guessButton;
    private JButton newGameButton;

    public HangmanUI() {
        game = new HangmanGame();
        setupUI();
        updateGameState();
    }

    private void setupUI() {
        setTitle("Hangman Game");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(400, 300);
        setLayout(new BorderLayout());

        // Create panels
        JPanel topPanel = new JPanel();
        JPanel centerPanel = new JPanel();
        JPanel bottomPanel = new JPanel();

        // Word display
        wordLabel = new JLabel("", SwingConstants.CENTER);
        wordLabel.setFont(new Font("Arial", Font.BOLD, 24));
        topPanel.add(wordLabel);

        // Status display
        attemptsLabel = new JLabel("", SwingConstants.CENTER);
        statusLabel = new JLabel("", SwingConstants.CENTER);
        centerPanel.setLayout(new BoxLayout(centerPanel, BoxLayout.Y_AXIS));
        centerPanel.add(attemptsLabel);
        centerPanel.add(statusLabel);

        // Input controls
        guessField = new JTextField(1);
        guessButton = new JButton("Guess");
        newGameButton = new JButton("New Game");

        bottomPanel.add(new JLabel("Guess a letter: "));
        bottomPanel.add(guessField);
        bottomPanel.add(guessButton);
        bottomPanel.add(newGameButton);

        // Add panels to frame
        add(topPanel, BorderLayout.NORTH);
        add(centerPanel, BorderLayout.CENTER);
        add(bottomPanel, BorderLayout.SOUTH);

        // Add action listeners
        guessButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String input = guessField.getText().toUpperCase();
                if (input.length() == 1 && Character.isLetter(input.charAt(0))) {
                    boolean correct = game.guessLetter(input.charAt(0));
                    updateGameState();
                    if (!correct) {
                        JOptionPane.showMessageDialog(HangmanUI.this, 
                            "Wrong guess! Try again.");
                    }
                    guessField.setText("");
                } else {
                    JOptionPane.showMessageDialog(HangmanUI.this, 
                        "Please enter a single letter.");
                }
            }
        });

        newGameButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                game.resetGame();
                updateGameState();
            }
        });
    }

    private void updateGameState() {
        wordLabel.setText(game.getCurrentWordState());
        attemptsLabel.setText("Remaining attempts: " + game.getRemainingAttempts());

        if (game.isGameOver()) {
            if (game.isGameWon()) {
                statusLabel.setText("Congratulations! You won!");
            } else {
                statusLabel.setText("Game Over! The word was: " + game.getWord());
            }
            guessButton.setEnabled(false);
        } else {
            statusLabel.setText("Keep guessing!");
            guessButton.setEnabled(true);
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                new HangmanUI().setVisible(true);
            }
        });
    }
} 