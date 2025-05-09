import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;
import java.util.HashSet;
import java.util.HashMap;

public class Room {
    private String name;
    private String word;
    private StringBuilder currentWordState;
    private int remainingAttempts;
    private List<Player> players;
    private List<Player> spectators;
    private boolean gameStarted;
    private boolean gameOver;
    private boolean gameWon;
    private int currentPlayerIndex;
    private int maxPlayers;
    private Map<Player, Set<Character>> playerGuesses;

    public Room(String name, int maxPlayers) {
        this.name = name;
        this.maxPlayers = maxPlayers;
        this.players = new ArrayList<>();
        this.spectators = new ArrayList<>();
        this.gameStarted = false;
        this.gameOver = false;
        this.gameWon = false;
        this.remainingAttempts = 6;
        this.currentPlayerIndex = 0;
        this.playerGuesses = new HashMap<>();
    }

    public synchronized boolean addPlayer(Player player) {
        if (players.size() < maxPlayers && !gameStarted) {
            players.add(player);
            playerGuesses.put(player, new HashSet<>());
            return true;
        }
        return false;
    }

    public synchronized boolean addSpectator(Player spectator) {
        if (!spectators.contains(spectator)) {
            spectators.add(spectator);
            return true;
        }
        return false;
    }

    public synchronized void removePlayer(Player player) {
        players.remove(player);
        playerGuesses.remove(player);
        if (players.isEmpty()) {
            gameStarted = false;
        }
    }

    public synchronized void removeSpectator(Player spectator) {
        spectators.remove(spectator);
    }

    public synchronized boolean startGame() {
        if (players.size() >= 2 && !gameStarted) {
            gameStarted = true;
            gameOver = false;
            gameWon = false;
            remainingAttempts = 6;
            currentPlayerIndex = 0;
            word = WordList.getRandomWord();
            currentWordState = new StringBuilder("_".repeat(word.length()));
            playerGuesses.clear();
            
            // Initialize guess sets for all players
            for (Player player : players) {
                playerGuesses.put(player, new HashSet<>());
            }
            return true;
        }
        return false;
    }

    public synchronized boolean makeGuess(Player player, char guess) {
        if (!gameStarted || !isValidPlayer(player) || !isCurrentPlayer(player) || gameOver) {
            return false;
        }

        // Add guess to player's set of guesses
        playerGuesses.computeIfAbsent(player, k -> new HashSet<>()).add(guess);

        boolean correctGuess = false;
        for (int i = 0; i < word.length(); i++) {
            if (Character.toLowerCase(word.charAt(i)) == Character.toLowerCase(guess)) {
                currentWordState.setCharAt(i, word.charAt(i));
                correctGuess = true;
            }
        }

        if (!correctGuess) {
            remainingAttempts--;
        }

        // Check if game is won
        if (currentWordState.toString().equals(word)) {
            gameOver = true;
            gameWon = true;
        }

        // Check if game is lost
        if (remainingAttempts <= 0) {
            gameOver = true;
            gameWon = false;
        }

        // Move to next player if guess was incorrect
        if (!correctGuess) {
            moveToNextPlayer();
        }

        return correctGuess;
    }

    private synchronized void moveToNextPlayer() {
        currentPlayerIndex = (currentPlayerIndex + 1) % players.size();
    }

    private synchronized boolean isCurrentPlayer(Player player) {
        if (!gameStarted) return false;
        Player currentPlayer = getCurrentPlayer();
        return currentPlayer != null && currentPlayer.equals(player);
    }

    public synchronized String getCurrentWordState() {
        return currentWordState != null ? currentWordState.toString() : "";
    }

    public synchronized int getRemainingAttempts() {
        return remainingAttempts;
    }

    public synchronized boolean isGameOver() {
        return gameOver;
    }

    public synchronized boolean isGameWon() {
        return gameWon;
    }

    public synchronized String getWord() {
        return word;
    }

    public synchronized String getName() {
        return name;
    }

    public synchronized List<Player> getPlayers() {
        return new ArrayList<>(players);
    }

    public synchronized List<Player> getSpectators() {
        return new ArrayList<>(spectators);
    }

    public synchronized Player getCurrentPlayer() {
        if (!gameStarted || players.isEmpty()) {
            return null;
        }
        return players.get(currentPlayerIndex);
    }

    public synchronized boolean isGameStarted() {
        return gameStarted;
    }

    public synchronized int getMaxPlayers() {
        return maxPlayers;
    }

    public synchronized int getPlayerCount() {
        return players.size();
    }

    public synchronized Set<Character> getPlayerGuesses(Player player) {
        return playerGuesses.getOrDefault(player, new HashSet<>());
    }

    private boolean isValidPlayer(Player player) {
        return players.contains(player);
    }
} 