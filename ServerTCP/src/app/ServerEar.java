package app;

import java.io.*;
import java.net.Socket;

public class ServerEar extends Thread {
    protected Socket socket;
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
        this.socket = clientSocket;
    }

    public void run() {
        try {
            in = this.getInput(socket);
            out = this.getOutput(socket);
        } catch (IOException e) {
            return;
        }
        String line;
        while (true) {
            try {
                line = in.readLine();
                if ((line == null) || line.equalsIgnoreCase("QUIT")) {
                    socket.close();
                    return;
                } else {
                    out.println(line);
                }
            } catch (IOException e) {
                e.printStackTrace();
                return;
            }
        }
    }
}