package test.hangman;
/**
 * UCID: [Your UCID]
 * Date: [Current Date]
 * GameRoom class that extends Room for game-specific functionality
 */
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

public class GameRoom extends Room {
    private String currentWord;
    private List<Character> guessedLetters;
    private int currentPlayerIndex;
    private List<Player> players;
    private boolean gameInProgress;
    private static final int MAX_STRIKES = 6;
    private List<String> wordList;
    private Random random;
    private boolean hardMode;
    private boolean strikeRemoval;
    private int turnTimer;
    private int maxRounds;
    private int currentRound;
    private Timer timer;

    public GameRoom(String name) {
        super(name);
        this.guessedLetters = new ArrayList<>();
        this.players = new ArrayList<>();
        this.gameInProgress = false;
        this.random = new Random();
        this.hardMode = false;
        this.strikeRemoval = false;
        this.turnTimer = 30;
        this.maxRounds = 5;
        this.currentRound = 0;
        loadWordList();
    }

    private void loadWordList() {
        wordList = new ArrayList<>();
        try (BufferedReader reader = new BufferedReader(new FileReader("words.txt"))) {
            String line;
            while ((line = reader.readLine()) != null) {
                wordList.add(line.trim().toLowerCase());
            }
        } catch (IOException e) {
            System.out.println("Error loading word list: " + e.getMessage());
        }
    }

    private String selectRandomWord() {
        if (wordList.isEmpty()) {
            loadWordList();
            if (wordList.isEmpty()) {
                return "hangman";
            }
        }
        int index = random.nextInt(wordList.size());
        String word = wordList.get(index);
        wordList.remove(index);
        return word;
    }

    public void handleReady(ServerThread client, boolean hardMode, boolean strikeRemoval) {
        this.hardMode = hardMode;
        this.strikeRemoval = strikeRemoval;

        if (!players.contains(client.getPlayer())) {
            players.add(client.getPlayer());
            System.out.println("Player " + client.getClientName() + " added to game. Total players: " + players.size());
        }

        boolean allReady = true;
        int readyCount = 0;
        for (ServerThread c : getClients()) {
            if (!c.isSpectator()) {
                if (c.isReady()) {
                    readyCount++;
                } else {
                    allReady = false;
                }
            }
        }
        System.out.println("Ready players: " + readyCount + ", All ready: " + allReady + ", Total players: " + players.size());

        if (allReady && players.size() >= 2) {
            System.out.println("All players ready, starting game!");
            startGame();
        } else {
            if (players.size() < 2) {
                broadcastMessage("Waiting for more players... (Need at least 2 players)");
            } else {
                broadcastMessage("Waiting for more players to be ready... (" + readyCount + "/" + players.size() + " ready)");
            }
        }
    }

    public void startGame() {
        if (!gameInProgress) {
            System.out.println("Starting game with " + players.size() + " players");
            gameInProgress = true;
            currentRound = 1;
            broadcastMessage("GAME_START");
            startRound();
        }
    }

    private void startRound() {
        guessedLetters.clear();
        currentPlayerIndex = new Random().nextInt(players.size());
        for (Player player : players) {
            player.setStrikes(0);
        }
        currentWord = selectRandomWord();

        timer = new Timer(this);
        timer.start();

        broadcastMessage("Round " + currentRound + " started!");
        String currentPlayerName = players.get(currentPlayerIndex).getName();
        broadcastMessage("TURN:" + currentPlayerName);
        broadcastMessage("It's " + currentPlayerName + "'s turn");

        broadcastMessage("Current word: " + getWordDisplay());
    }

    public void handleGuess(String guess, ServerThread sender) {
        if (!gameInProgress) return;

        Player currentPlayer = players.get(currentPlayerIndex);
        if (!currentPlayer.getClientId().equals(sender.getClientId())) {
            sender.sendMessage("It's not your turn!");
            return;
        }
        if (guess.length() == 1) {
            handleLetterGuess(guess.charAt(0), sender);
        } else {
            handleWordGuess(guess, sender);
        }
    }

    public void skipTurn(ServerThread sender) {
        if (!gameInProgress) return;

        Player currentPlayer = players.get(currentPlayerIndex);
        if (!currentPlayer.getClientId().equals(sender.getClientId())) {
            sender.sendMessage("It's not your turn!");
            return;
        }

        broadcastMessage(sender.getClientName() + " skipped their turn.");
        nextTurn();
    }

    private void handleLetterGuess(char letter, ServerThread sender) {
        char lowerLetter = Character.toLowerCase(letter);
        if (!hardMode && (guessedLetters.contains(lowerLetter) || guessedLetters.contains(Character.toUpperCase(letter)))) {
            sender.sendMessage("Letter already guessed!");
            return;
        }

        guessedLetters.add(lowerLetter);
        broadcastMessage("LETTER_USED:" + letter);
        int occurrences = countLetterOccurrences(lowerLetter);
        
        Player player = getPlayer(sender.getClientId());

        if (occurrences > 0) {
            int points = occurrences * 10;
            player.addPoints(points);
            broadcastMessage(sender.getClientName() + " guessed " + letter +
                           " and found " + occurrences + " occurrences for " + points + " points!");

            broadcastMessage("Current word: " + getWordDisplay());

            if (strikeRemoval && player.getStrikes() > 0) {
                player.decrementStrikes();
                broadcastMessage("Strike removed! " + sender.getClientName() + " now has " + player.getStrikes() + " strikes.");
            }
        } else {
            player.incrementStrikes();
            broadcastMessage(sender.getClientName() + " guessed " + letter + 
                " which isn't in the word. Strikes: " + player.getStrikes());
            sender.sendMessage("STRIKES:" + player.getStrikes());
        }

        checkGameState();
    }

    private void handleWordGuess(String word, ServerThread sender) {
        if (word.equalsIgnoreCase(currentWord)) {
            int points = countMissingLetters() * 20;
            Player player = getPlayer(sender.getClientId());
            player.addPoints(points);
            broadcastMessage(sender.getClientName() + " guessed the correct word and got " +
                           points + " points!");
            endRound();
        } else {
            Player player = getPlayer(sender.getClientId());
            player.incrementStrikes();
            broadcastMessage(sender.getClientName() + " guessed " + word +
               " which is wrong. Strikes: " + player.getStrikes());
            sender.sendMessage("STRIKES:" + player.getStrikes());
            checkGameState();
        }
    }

    private int countLetterOccurrences(char letter) {
        int count = 0;
        char lowerLetter = Character.toLowerCase(letter);
        for (char c : currentWord.toCharArray()) {
            if (Character.toLowerCase(c) == lowerLetter) {
                count++;
            }
        }
        return count;
    }

    private int countMissingLetters() {
        int count = 0;
        for (char c : currentWord.toCharArray()) {
            if (!guessedLetters.contains(Character.toLowerCase(c))) {
                count++;
            }
        }
        return count;
    }

    private String getWordDisplay() {
        StringBuilder display = new StringBuilder();
        for (char c : currentWord.toCharArray()) {
            if (guessedLetters.contains(Character.toLowerCase(c)) || guessedLetters.contains(Character.toUpperCase(c))) {
                display.append(c);
            } else {
                display.append('_');
            }
            display.append(' ');
        }
        return display.toString().trim();
    }

    private void checkGameState() {
        if (players.get(currentPlayerIndex).getStrikes() >= MAX_STRIKES) {
            broadcastMessage("Game over! The word was: " + currentWord);
            endRound();
        } else if (countMissingLetters() == 0) {
            broadcastMessage("Word completed! The word was: " + currentWord);
            endRound();
        } else {
            boolean anyActivePlayers = false;
            for (Player player : players) {
                if (!player.isAway()) {
                    anyActivePlayers = true;
                    break;
                }
            }

            if (!anyActivePlayers) {
                broadcastMessage("All players are away. Ending round.");
                endRound();
            } else {
                nextTurn();
            }
        }
    }

    private void nextTurn() {
        do {
            currentPlayerIndex = (currentPlayerIndex + 1) % players.size();
        } while (players.get(currentPlayerIndex).isAway());

        timer.reset();
        String currentPlayerName = players.get(currentPlayerIndex).getName();
        broadcastMessage("TURN:" + currentPlayerName);
        broadcastMessage("It's " + currentPlayerName + "'s turn");
    }

    private void endRound() {
        timer.stopTimer();
        broadcastScoreboard();
        currentRound++;

        if (currentRound > maxRounds) {
            endGame();
        } else {
            startRound();
        }
    }

    private void endGame() {
        gameInProgress = false;
        broadcastMessage("GAME_OVER");
        broadcastFinalScoreboard();
        resetPlayers();
    }

    private void broadcastScoreboard() {
        players.sort((p1, p2) -> Integer.compare(p2.getPoints(), p1.getPoints()));
        StringBuilder scoreboard = new StringBuilder("Scoreboard:\n");
        for (Player player : players) {
            scoreboard.append(player.getName()).append(": ").append(player.getPoints()).append(" points\n");
        }
        broadcastMessage(scoreboard.toString());
    }

    private void broadcastFinalScoreboard() {
        players.sort((p1, p2) -> Integer.compare(p2.getPoints(), p1.getPoints()));
        StringBuilder scoreboard = new StringBuilder("Final Scoreboard:\n");
        for (Player player : players) {
            scoreboard.append(player.getName()).append(": ").append(player.getPoints()).append(" points\n");
        }
        broadcastMessage(scoreboard.toString());
    }

    private void resetPlayers() {
        for (Player player : players) {
            player.setPoints(0);
        }
        for (ServerThread client : getClients()) {
            client.setReady(false);
        }
    }

    private Player getPlayer(String clientId) {
        for (Player player : players) {
            if (player.getClientId().equals(clientId)) {
                return player;
            }
        }
        return null;
    }

    private class Timer extends Thread {
        private GameRoom gameRoom;
        private int remainingTime;
        private volatile boolean running;

        public Timer(GameRoom gameRoom) {
            this.gameRoom = gameRoom;
            this.remainingTime = turnTimer;
            this.running = true;
        }

        @Override
        public void run() {
            while (running && remainingTime > 0) {
                try {
                    Thread.sleep(1000);
                    remainingTime--;
                    broadcastMessage("TIMER:" + remainingTime);
                } catch (InterruptedException e) {
                    break;
                }
            }
            if (running) {
                gameRoom.nextTurn();
            }
        }

        public void reset() {
            remainingTime = turnTimer;
        }

        public void stopTimer() {
            running = false;
            interrupt();
        }
    }

    public void handlePlayerDisconnect(ServerThread client) {
        Player player = getPlayer(client.getClientId());
        if (player != null) {
            player.setAway(true);
            broadcastMessage(player.getName() + " has disconnected.");
            checkGameState();
        }
    }

    public void handlePlayerReconnect(ServerThread client) {
        Player player = getPlayer(client.getClientId());
        if (player != null) {
            player.setAway(false);
            broadcastMessage(player.getName() + " has reconnected.");
        }
    }
}
