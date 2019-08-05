package app;

import app.model.Client;

import java.io.*;
import java.net.*;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;

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
        this.serverEars.forEach(ServerEar::kill);
        this.serverEars.clear();
        System.exit(1);
    }

    void killUser(String username) {
        for (Iterator<ServerEar> iterator = this.serverEars.iterator(); iterator.hasNext();) {
            ServerEar serverEar = iterator.next();
            if(serverEar.client.getName().equals(username)) {
                serverEar.kickUser();
                iterator.remove();
            }
        }
    }
}
