package app;

import java.io.IOException;
import java.net.*;

public class BroadcastingServer implements Runnable {
    private BroadcastListener bl;

    @Override
    public void run() {
        try {
            bl = new BroadcastListener();
            bl.listen();
        } catch (Exception e) {
            bl.close();
            e.printStackTrace();
        }
    }

    class BroadcastListener {
        DatagramSocket socket;

        BroadcastListener() throws SocketException, UnknownHostException {
            socket = new DatagramSocket(2001, InetAddress.getByName("0.0.0.0"));
            socket.setBroadcast(true);
        }

        void listen() throws IOException {
            System.out.println("Listening to clients broadcast.");

            //Receive a packet
            byte[] recvBuf = new byte[15000];
            DatagramPacket packet = new DatagramPacket(recvBuf, recvBuf.length);

            socket.receive(packet); // This method blocks until a datagram is received

            greetBroadcaster(packet);

            if (!socket.isClosed()) {
                listen();
            }
        }

        private void greetBroadcaster (DatagramPacket packet) throws IOException {
            byte[] sendData = "SERVER_SCAN".getBytes();
            //Send a response
            DatagramPacket sendPacket = new DatagramPacket(sendData, sendData.length, packet.getAddress(), packet.getPort());
            socket.send(sendPacket);
        }

        void close () {
            socket.close();
        }
    }
}