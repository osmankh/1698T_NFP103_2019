package app;

import java.net.*;

public class BroadcastingClient implements Runnable {
    @Override
    public void run() {
        try {
            //Open a random port to send the package
            DatagramSocket c = new DatagramSocket();
            c.setBroadcast(true);

            byte[] sendData = "SERVER_SCAN".getBytes();

            /*
             * Try the 255.255.255.255 broadcast
             * (or use the boradcas address of you network class like 192.168.1.255)
             * port 9100
             */
            DatagramPacket sendPacket = new DatagramPacket(sendData, sendData.length,
                    InetAddress.getByName("255.255.255.255"), 2001);
            c.send(sendPacket);

            //Wait for a response
            byte[] recvBuf = new byte[15000];
            DatagramPacket receivePacket = new DatagramPacket(recvBuf, recvBuf.length);
            c.receive(receivePacket);

            //We have a response
            System.out.println("Available server : " + receivePacket.getAddress().getHostAddress());

            /*
             *  NOW you have the server IP in receivePacket.getAddress()
             */

            //Close the port!
            c.close();
            System.out.println("Fetching end.");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}