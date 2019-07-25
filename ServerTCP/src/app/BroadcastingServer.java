package app;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;

public class BroadcastingServer implements Runnable {
    @Override
    public void run() {
        try {
            /*
             * open receive datagram broadcast socket port 9100
             */
            DatagramSocket socket = new DatagramSocket(2001, InetAddress.getByName("0.0.0.0"));
            socket.setBroadcast(true);

            //Receive a packet
            byte[] recvBuf = new byte[15000];
            DatagramPacket packet = new DatagramPacket(recvBuf, recvBuf.length);
            socket.receive(packet); // This method blocks until a datagram is received

            byte[] sendData = "SERVER_SCAN".getBytes();
            //Send a response
            DatagramPacket sendPacket = new DatagramPacket(sendData, sendData.length, packet.getAddress(), packet.getPort());
            socket.send(sendPacket);

            // close socket
            socket.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}