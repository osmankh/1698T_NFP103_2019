package app.model;

import java.net.Socket;

public class Client {
    protected Socket socket;
    protected String name;

    public Client (String name, Socket socket) {
        this.socket = socket;
        this.name = name;
    }

    public Socket getSocket() {
        return this.socket;
    }

    public String getName() {
        return this.name;
    }
}
