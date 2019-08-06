package app;

import app.model.Client;

import java.io.*;
import java.net.*;
import java.util.ArrayList;
import java.util.List;

import static java.lang.System.out;

public class ServerTCP {
    private List<ServerEar> serverEars;

    private ServerTCP() {
        this.serverEars = new ArrayList<>();
    }

    private void start(int port) {
        ServerSocket serverSocket = null;
        Socket socket = null;

        try {
            serverSocket = new ServerSocket(port);
            out.println("[INFO] Server started...");
            out.println("[INFO] Listening for connections at " + serverSocket.getInetAddress() + ":" + serverSocket.getLocalPort());
        } catch (IOException e) {
            e.printStackTrace();

        }
        while (true) {
            try {
                assert serverSocket != null;
                socket = serverSocket.accept();
            } catch (IOException e) {
                out.println("I/O error: " + e);
            }

            this.hireServerEar(socket);
        }
    }

    private void hireServerEar(Socket socket) {
        ServerEar ear = new ServerEar(socket, this);
        ear.start();
        this.serverEars.add(ear);
    }

    public static void main(String[] args) {
        ServerTCP server = new ServerTCP();

        Thread bs = new Thread(new BroadcastingServer());
        bs.start();

        Thread inputHandler = new Thread(new InputHandler(server));
        inputHandler.start();

        server.start(2000);
    }

    void sendMessageToAllConnectedUsers(String message, Client client) {
        serverEars.forEach(serverEar -> serverEar.sendMessage(message, client));
    }

    void notifyAllUsers (String message) {
        serverEars.forEach(serverEar -> serverEar.notifyUser(message));
    }

    void removeClient(ServerEar serverEar) {
        this.serverEars.remove(serverEar);
    }

    List<ServerEar> getServerEars() {
        return this.serverEars;
    }

    void shutdown() {
        out.println("[INFO] Preparing server for shutdown.");
        out.println("[INFO] Killing connected users connections.");
        while (this.serverEars.size() > 0) {
            this.serverEars.get(0).kill();
        }
        out.println("[INFO] All users disconnected.");
        out.println("[INFO] Shutdown server...");
        System.exit(1);
    }

    void killUser(String username) {
        ServerEar earToRemove = null;
        for (ServerEar serverEar : this.serverEars) {
            if (serverEar.client.getName().equals(username)) {
                earToRemove = serverEar;
            }
        }
        earToRemove.kickUser();
        this.notifyAllUsers("[SERVER -> INFO] " + earToRemove.client.getName() + " Has been kicked out.");
    }
}
