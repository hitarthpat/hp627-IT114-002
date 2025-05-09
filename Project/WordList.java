import java.util.Random;

public class WordList {
    private static final String[] WORDS = {
        "PROGRAMMING", "JAVA", "COMPUTER", "ALGORITHM", "DATABASE",
        "NETWORK", "INTERFACE", "VARIABLE", "FUNCTION", "METHOD",
        "OBJECT", "CLASS", "INHERITANCE", "POLYMORPHISM", "ENCAPSULATION",
        "ABSTRACTION", "EXCEPTION", "THREAD", "SOCKET", "SERVER"
    };

    private static final Random random = new Random();

    public static String getRandomWord() {
        return WORDS[random.nextInt(WORDS.length)];
    }
} 