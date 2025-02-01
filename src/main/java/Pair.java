import java.time.LocalDateTime;
public class Pair {
    private final String value;
    private final long timestamp;

    public Pair(String value, long time) {
        this.value = value;
        this.timestamp = time; // Capture the current time
    }

    public String getValue() {
        return value;
    }

    public long getTimestamp() {
        return timestamp;
    }

    @Override
    public String toString() {
        return value + " (stored at " + timestamp + ")";
    }
}
