package test.hangman;
/**
 * UCID: [Your UCID]
 * Date: [Current Date]
 * Base Payload class for message handling between client and server
 */
public class Payload {
    private String type;
    private String message;
    private String clientId;

    public Payload(String type, String message, String clientId) {
        this.type = type;
        this.message = message;
        this.clientId = clientId;
    }

    public String getType() {
        return type;
    }

    public String getMessage() {
        return message;
    }

    public String getClientId() {
        return clientId;
    }

    @Override
    public String toString() {
        return "Payload{" +
                "type='" + type + '\'' +
                ", message='" + message + '\'' +
                ", clientId='" + clientId + '\'' +
                '}';
    }
} 