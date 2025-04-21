/**
 * UCID: [Your UCID]
 * Date: [Current Date]
 * Player class to handle player-specific data
 */
public class Player {
    private String clientId;
    private String name;
    private int points;
    private boolean isAway;
    private boolean isSpectator;
    private int strikes = 0;


    public Player(String clientId, String name) {
        this.clientId = clientId;
        this.name = name;
        this.points = 0;
        this.isAway = false;
        this.isSpectator = false;
    }

    public String getClientId() {
        return clientId;
    }

    public String getName() {
        return name;
    }

    public int getPoints() {
        return points;
    }

    public void setPoints(int points) {
        this.points = points;
    }

    public void addPoints(int points) {
        this.points += points;
    }

    public boolean isAway() {
        return isAway;
    }

    public void setAway(boolean away) {
        isAway = away;
    }

    public boolean isSpectator() {
        return isSpectator;
    }

    public void setSpectator(boolean spectator) {
        isSpectator = spectator;
    }
////
    public int getStrikes() {
    return strikes;
    }

    public void setStrikes(int strikes) {
    this.strikes = strikes;
    }

    public void incrementStrikes() {
    this.strikes++;
    }

    public void decrementStrikes() {
    if (this.strikes > 0) this.strikes--;
    }
} 