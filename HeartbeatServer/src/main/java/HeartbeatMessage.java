import java.io.Serializable;
import java.time.Instant;

public class HeartbeatMessage implements Serializable {
    private int sequenceNumber;
    private Instant time;

    public HeartbeatMessage(int sequenceNumber, Instant time) {
        this.sequenceNumber = sequenceNumber;
        this.time = time;
    }

    public int getSequenceNumber() {
        return sequenceNumber;
    }

    public Instant getTime() {
        return time;
    }
}

