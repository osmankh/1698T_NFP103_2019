package app;

import java.io.*;
import java.net.*;
import java.util.ArrayList;
import java.util.List;

import static java.lang.System.out;

public class ServerTCP {
    private List<ServerEar> serverEars;

    public ServerTCP() {
        this.serverEars = new ArrayList<>();
    }

    private void start(int port) {
        ServerSocket serverSocket = null;
        Socket socket = null;

        try {
            serverSocket = new ServerSocket(port);
        } catch (IOException e) {
            e.printStackTrace();

        }
        while (true) {
            try {
                socket = serverSocket.accept();
            } catch (IOException e) {
                out.println("I/O error: " + e);
            }

            this.hireServerEar(socket);
        }
    }

    private void hireServerEar(Socket socket) {
        ServerEar ear = new ServerEar(socket);
        ear.start();
        this.serverEars.add(ear);
    }

    public static void main(String[] args) {
        ServerTCP server = new ServerTCP();
        Thread bs = new Thread(new BroadcastingServer());
        bs.start();
        server.start(2000);
    }
}
