package app;

import app.model.Client;

import java.io.*;
import java.net.Socket;

public class ServerEar extends Thread {
    protected Client client;
    private PrintWriter out;
    private BufferedReader in;

    private BufferedReader getInput(Socket p) throws IOException
    {
        return new BufferedReader(new InputStreamReader(p.getInputStream()));
    }

    private PrintWriter getOutput(Socket p) throws IOException
    {
        return new PrintWriter(new OutputStreamWriter(p.getOutputStream()), true);
    }

    public ServerEar(Socket clientSocket) {
        try {
            this.in = this.getInput(clientSocket);
            this.out = this.getOutput(clientSocket);
            this.client = new Client(in.readLine(), clientSocket);
            System.out.printf("New Client connected => Name : %s", this.client.getName());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void run() {
        String line;
        while (true) {
            try {
                line = in.readLine();
                if(!this.checkMessage(line)) {
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
        } else {
            this.out.printf("unrecognised message %s", message);
            this.out.println();
        }

        return true;
    }

    private void quitClient() {
        this.out.println("bye");
        this.out.printf("Client with address %s disconnected!", this.client.getSocket().getRemoteSocketAddress());
        try {
            this.stopConnection();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void stopConnection() throws IOException {
        in.close();
        this.out.close();
        this.client.getSocket().close();
    }
}