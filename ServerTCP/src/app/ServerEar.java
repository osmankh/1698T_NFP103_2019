package app;

import app.model.Client;

import java.io.*;
import java.net.Socket;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

public class ServerEar extends Thread {
    protected Client client;
    protected ServerTCP serverTCP;
    private PrintWriter out;
    private BufferedReader in;

    private BufferedReader getInput(Socket p) throws IOException {
        return new BufferedReader(new InputStreamReader(p.getInputStream()));
    }

    private PrintWriter getOutput(Socket p) throws IOException {
        return new PrintWriter(new OutputStreamWriter(p.getOutputStream()), true);
    }

    public ServerEar(Socket clientSocket, ServerTCP serverTCP) {
        try {
            this.in = this.getInput(clientSocket);
            this.out = this.getOutput(clientSocket);
            this.client = new Client(in.readLine(), clientSocket);
            this.serverTCP = serverTCP;
            this.serverTCP.notifyAllUsers("New user connected. `Name: " + this.client.getName() + "`");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void run() {
        String line;
        while (true) {
            try {
                line = in.readLine();
                if (!this.checkMessage(line)) {
                    this.stopConnection();
                    break;
                }
            } catch (IOException e) {
                try {
                    this.stopConnection();
                } catch (IOException ignored) {
                }
                break;
            }
        }
    }

    private boolean checkMessage(String message) {
        if ("_quit".equals(message)) {
            this.quitClient();
            return false;
        } else if ("_who".equals(message)) {
            this.sendConnectedUsers();
        } else {
            this.serverTCP.sendMessageToAllConnectedUsers(message, this.client);
        }
        return true;
    }

    private void sendConnectedUsers() {
        final String[] response = {"Connected users: \n"};
        this.serverTCP.getServerEars().forEach(ServerEar -> response[0] += "\t- " + ServerEar.client.getName() + " ( Address: " + ServerEar.client.getSocket().getInetAddress() + ":" + ServerEar.client.getSocket().getPort() + " )\n");
        this.out.println(response[0]);
    }

    private String getDate() {
        DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
        Date date = new Date();
        return dateFormat.format(date);
    }

    private void quitClient() {
        this.out.println("_quit");
        try {
            this.stopConnection();
        } catch (IOException e) {
            e.printStackTrace();
        }
        this.serverTCP.notifyAllUsers(this.client.getName() + " has been disconnected!");
    }

    public void stopConnection() throws IOException {
        in.close();
        this.out.close();
        this.client.getSocket().close();
        this.serverTCP.removeClient(this);
    }

    public void sendMessage(String message, Client client) {
        this.out.printf("%s : %s > %s", this.getDate(), client.getName(), message);
        this.out.println();
    }

    public void notifyUser(String message) {
        this.out.println(message);
    }
}