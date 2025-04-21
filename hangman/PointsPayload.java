/**
 * UCID: [Your UCID]
 * Date: [Current Date]
 * PointsPayload class for handling game points
 */
public class PointsPayload extends Payload {
    private int points;

    public PointsPayload(String type, String message, String clientId, int points) {
        super(type, message, clientId);
        this.points = points;
    }

    public int getPoints() {
        return points;
    }

    @Override
    public String toString() {
        return "PointsPayload{" +
                "type='" + getType() + '\'' +
                ", message='" + getMessage() + '\'' +
                ", clientId='" + getClientId() + '\'' +
                ", points=" + points +
                '}';
    }
} 