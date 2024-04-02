import java.io.ByteArrayInputStream;
import java.io.ObjectInputStream;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.time.Duration;
import java.time.Instant;
import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;

public class HeartbeatServer {

    private static Instant lastPacketReceivedTime = Instant.now();

    public static void main(String[] args) throws Exception {
        DatagramSocket serverSocket = new DatagramSocket(12000);
        byte[] receiveData = new byte[4096];
        int currentSequence = 1;

        Timer timer = new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                if (Duration.between(lastPacketReceivedTime, Instant.now()).getSeconds() > 10) {
                    System.out.println("No packets received for 10 seconds. Client stopped.");
                    serverSocket.close();
                    timer.cancel();
                }
            }
        }, 1000, 1000);

        while (true) {
            try {
                DatagramPacket receivePacket = new DatagramPacket(receiveData, receiveData.length);
                serverSocket.receive(receivePacket);

                lastPacketReceivedTime = Instant.now();

                // Simulate randomly dropping packets
                if (new Random().nextInt(10) < 3) {
                    continue;
                }

                ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(receivePacket.getData());
                ObjectInputStream objectInputStream = new ObjectInputStream(byteArrayInputStream);
                HeartbeatMessage message = (HeartbeatMessage) objectInputStream.readObject();

                int sequenceNumber = message.getSequenceNumber();
                Instant timeSent = message.getTime();

                while (currentSequence < sequenceNumber) {
                    System.out.println("Lost heartbeat for SEQ #" + currentSequence);
                    currentSequence++;
                }
                currentSequence = sequenceNumber + 1;

                long oneWayTime = Duration.between(timeSent, Instant.now()).toMillis();
                System.out.println("Heartbeat for SEQ #" + sequenceNumber + " received after " + oneWayTime + " ms");
            } catch (Exception e) {
                break;
            }
        }
    }
}


