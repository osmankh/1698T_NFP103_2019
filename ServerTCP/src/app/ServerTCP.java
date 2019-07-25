package app;

import java.io.*;
import java.net.*;
import java.util.ArrayList;
import java.util.List;

import static java.lang.System.out;

public class ServerTCP {
    private ServerSocket serverSocket;
    private List<ServerEar> serverEars;

    public ServerTCP() {
        this.serverEars = new ArrayList<>();
    }

    private void start(int port) {
//        serverSocket = new ServerSocket(port);
//        this.printServerAddress();
//
//        clientSocket = serverSocket.accept();
//        this.printClientAddress();
//
//        out = this.getOutput(clientSocket);
//        in = this.getInput(clientSocket);
//        String message;
//
//        while ((message = in.readLine()) != null) {
//            System.out.println("Receiving data from client");
//            if(!this.checkMessage(message)) {
//            	this.stop();
//            	break;
//            }
//        }

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

//
//    private void printServerAddress() {
//        out.printf("L'adresse de la socket d'serverSocket est %s\n",
//                serverSocket.getLocalSocketAddress());
//    }
//
//    private void printClientAddress() {
//        out.printf("L'adresse de la socket client (remote) est %s\n",
//                clientSocket.getRemoteSocketAddress());
//    }

    public static void main(String[] args) throws IOException {
        ServerTCP server = new ServerTCP();
        Thread bs = new Thread(new BroadcastingServer());
        bs.start();
        server.start(2000);
    }
}
