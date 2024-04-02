import java.io.ByteArrayOutputStream;
import java.io.ObjectOutputStream;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.time.Instant;

public class HeartbeatClient {

    public static void main(String[] args) throws Exception {
        final int NUM_REQUESTS = 10;
        DatagramSocket clientSocket = new DatagramSocket();

        for (int sequenceNumber = 1; sequenceNumber <= NUM_REQUESTS; sequenceNumber++) {
            Instant requestTime = Instant.now();
            System.out.println("Sent ping with SEQ #" + sequenceNumber + " at " + requestTime);
            HeartbeatMessage message = new HeartbeatMessage(sequenceNumber, requestTime);
            byte[] sendData = serialize(message);
            DatagramPacket sendPacket = new DatagramPacket(sendData, sendData.length, InetAddress.getByName("127.0.0.1"), 12000);
            clientSocket.send(sendPacket);
            Thread.sleep(500);
        }
    }

    private static byte[] serialize(Object obj) throws Exception {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        ObjectOutputStream objectOutputStream = new ObjectOutputStream(byteArrayOutputStream);
        objectOutputStream.writeObject(obj);
        return byteArrayOutputStream.toByteArray();
    }
}
