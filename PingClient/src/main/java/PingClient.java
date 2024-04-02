import java.net.*;
import java.util.ArrayList;
import java.util.Collections;

class PingClient {

    private static final String CRLF = "\r\n";

    public static void main(String argv[]) throws SocketException, UnknownHostException {

        DatagramSocket clientSocket;
        DatagramPacket sendPacket;
        DatagramPacket receivePacket;
        byte[] receiveData = new byte[1024];
        byte[] sendData;
        InetAddress IPAddress;

        ArrayList<Long> rttList = new ArrayList<Long>();
        String server;
        int port;
        int timeout = 1_000;

        Integer sequence = 0;
        Integer pTransmitted = 0;
        Integer pLost = 0;

        if (argv.length < 2) {
            System.out.println("Usage: java PingServer hostname port\n");
            System.exit(-1);
        }

        server = argv[0];
        port = Integer.parseInt(argv[1]);

        clientSocket = new DatagramSocket();
        clientSocket.setSoTimeout(timeout);
        IPAddress = InetAddress.getByName(server);

        while (sequence < 10) {
            ++sequence;

            Long RTTb = System.currentTimeMillis();

            sendData = ("PING "+sequence).getBytes();

            try {

                sendPacket = new DatagramPacket(sendData, sendData.length, IPAddress, port);
                clientSocket.send(sendPacket);
                ++pTransmitted;

                receivePacket = new DatagramPacket(receiveData, receiveData.length);
                clientSocket.receive(receivePacket);
                String serverSentence = new String(receivePacket.getData(), 0, receivePacket.getLength());

                Long RTTa = System.currentTimeMillis();

                System.out.println(serverSentence);

                Long rtt = (RTTa - RTTb);
                rttList.add(rtt);
                System.out.println("RTT: "+ rtt +" ms"+CRLF);

            } catch (Exception e) {
                ++pLost;
                System.out.println("Request timed out."+CRLF);

            }


        }

        Integer pReceived = pTransmitted-pLost;
        Double pPacketLoss = (((float)pLost/pTransmitted)*100.0);
        Long min = Collections.min(rttList);
        Long max = Collections.max(rttList);
        Long ave = computeAverage(rttList);

        System.out.println("--- ping statistics ---"+CRLF+CRLF
                +pTransmitted+" packets transmitted, "
                +pReceived+" received, "+pPacketLoss+"% packet loss"
                +CRLF+CRLF+"rtt min/avg/max = "+min+" / "+ave+" / "
                +max.toString()+" ms"+CRLF);

        clientSocket.close();
    }

    private static Long computeAverage(ArrayList<Long> lst) {

        Long total = (long) 0;

        for (Long l: lst) {
            total += l;
        }

        return total/lst.size();
    }

}