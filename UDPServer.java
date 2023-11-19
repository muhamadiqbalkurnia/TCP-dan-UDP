import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.util.ArrayList;

public class UDPServer {
    public static final int SERVER_PORT = 7778;
    public static ArrayList<ClientInfo> clientsConnected = new ArrayList<>();
    public static DatagramSocket udpServerSocket;

    public static void main(String[] args) {
        try {
            udpServerSocket = new DatagramSocket(SERVER_PORT);
            System.out.println("Server has started");

            while (true) {
                byte[] receiveData = new byte[1024];
                DatagramPacket receivePacket = new DatagramPacket(receiveData, receiveData.length);

                udpServerSocket.receive(receivePacket);

                if (receivePacket != null) {
                    handleClient(receivePacket);
                }
                receiveData = new byte[1024];  // flush packet
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static void handleClient(DatagramPacket receivePacket) {
        String message = new String(receivePacket.getData(), 0, receivePacket.getLength());
        System.out.println("Received message: " + message);

        int replyPort = receivePacket.getPort();
        DatagramPacket okPacket = new DatagramPacket("OK".getBytes(), 2, receivePacket.getAddress(), replyPort);
        try {
            udpServerSocket.send(okPacket);
        } catch (IOException e) {
            e.printStackTrace();
        }

        // Simpan informasi klien yang terhubung
        ClientInfo clientInfo = new ClientInfo(receivePacket.getAddress(), replyPort);
        clientsConnected.add(clientInfo);

        // Kirim pesan ke semua klien yang terhubung
        broadcastMessage(receivePacket, message);
    }

    private static void broadcastMessage(DatagramPacket receivePacket, String message) {
        byte[] sendData = message.getBytes();
        InetAddress senderAddress = receivePacket.getAddress();
        int senderPort = receivePacket.getPort();

        for (ClientInfo client : clientsConnected) {
            // Periksa apakah klien yang dituju bukan pengirim pesan
            if (!(client.getAddress().equals(senderAddress) && client.getPort() == senderPort)) {
                try {
                    udpServerSocket.send(new DatagramPacket(sendData, sendData.length, client.getAddress(), client.getPort()));
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}

class ClientInfo {
    private InetAddress address;
    private int port;

    public ClientInfo(InetAddress address, int port) {
        this.address = address;
        this.port = port;
    }

    public InetAddress getAddress() {
        return address;
    }

    public int getPort() {
        return port;
    }
}
