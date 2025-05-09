import java.util.HashSet;
import java.util.Set;

public class HangmanGame {
    private String word;
    private Set<Character> guessedLetters;
    private int remainingAttempts;
    private WordList wordList;

    public HangmanGame() {
        wordList = new WordList();
        resetGame();
    }

    public void resetGame() {
        word = wordList.getRandomWord();
        guessedLetters = new HashSet<>();
        remainingAttempts = 6;
    }

    public boolean guessLetter(char letter) {
        letter = Character.toUpperCase(letter);
        if (guessedLetters.contains(letter)) {
            return false;
        }

        guessedLetters.add(letter);
        if (!word.contains(String.valueOf(letter))) {
            remainingAttempts--;
            return false;
        }
        return true;
    }

    public String getCurrentWordState() {
        StringBuilder result = new StringBuilder();
        for (char c : word.toCharArray()) {
            if (guessedLetters.contains(c)) {
                result.append(c);
            } else {
                result.append('_');
            }
            result.append(' ');
        }
        return result.toString().trim();
    }

    public boolean isGameWon() {
        for (char c : word.toCharArray()) {
            if (!guessedLetters.contains(c)) {
                return false;
            }
        }
        return true;
    }

    public boolean isGameOver() {
        return remainingAttempts <= 0 || isGameWon();
    }

    public int getRemainingAttempts() {
        return remainingAttempts;
    }

    public String getWord() {
        return word;
    }

    public Set<Character> getGuessedLetters() {
        return guessedLetters;
    }
} 