package app;

import app.model.Client;

import java.io.*;
import java.net.Socket;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

public class ServerEar extends Thread {
    protected Client client;
    private ServerTCP serverTCP;
    private PrintWriter out;
    private BufferedReader in;

    private BufferedReader getInput(Socket p) throws IOException {
        return new BufferedReader(new InputStreamReader(p.getInputStream()));
    }

    private PrintWriter getOutput(Socket p) throws IOException {
        return new PrintWriter(new OutputStreamWriter(p.getOutputStream()), true);
    }

    ServerEar(Socket clientSocket, ServerTCP serverTCP) {
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
            if (!this.client.getSocket().isConnected() || this.client.getSocket().isClosed()) {
                break;
            }

            try {
                line = in.readLine();
                if (!this.checkMessage(line)) {
                    this.stopConnection();
                    this.serverTCP.removeClient(this);
                    break;
                }
            } catch (IOException e) {
                try {
                    this.stopConnection();
                    this.serverTCP.removeClient(this);
                } catch (IOException ignored) {
                }
                break;
            }
        }
    }

    private boolean checkMessage(String message) {
        if (message == null) {
            return false;
        }
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
        ServerEar _this = this;
        this.serverTCP.getServerEars().forEach(ServerEar -> {
            response[0] += "\t- ";
            if (ServerEar == _this) {
                response[0] +=  "Me as " + ServerEar.client.getName();
            } else {
                response[0] +=  ServerEar.client.getName();
            }
            response[0] += " ( Address: " + ServerEar.client.getSocket().getInetAddress() + ":" + ServerEar.client.getSocket().getPort() + " )\n";
        });
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
            this.serverTCP.removeClient(this);
        } catch (IOException e) {
            e.printStackTrace();
        }
        this.serverTCP.notifyAllUsers(this.client.getName() + " has been disconnected!");
    }

    private void stopConnection() throws IOException {
        in.close();
        this.out.close();
        this.client.getSocket().close();
        this.interrupt();
    }

    void sendMessage(String message, Client client) {
        this.out.printf("%s : %s > %s", this.getDate(), client.getName(), message);
        this.out.println();
    }

    void notifyUser(String message) {
        this.out.println(message);
    }

    void kill() {
        try {
            this.out.println("[WARN] Your connection closed due to server shutdown.");
            this.out.println("_kill");
            this.stopConnection();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void kickUser() {
        try {
            this.out.println("[WARN] You have been kicked out.");
            this.out.println("_kill");
            this.stopConnection();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}