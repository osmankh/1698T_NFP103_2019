package app;

import java.io.*;
import java.net.Socket;
import java.net.UnknownHostException;

public class ClientTCP {
	private Socket clientSocket;
    private PrintWriter out;
    private BufferedReader in;
    
    private BufferedReader getInput() throws IOException
    {
        return new BufferedReader(new InputStreamReader(this.clientSocket.getInputStream()));
    }

    private PrintWriter getOutput() throws IOException
    {
        return new PrintWriter(new OutputStreamWriter(this.clientSocket.getOutputStream()));
    }
    
    public void start(String ip, int port) throws UnknownHostException, IOException {
    	clientSocket = new Socket(ip, port);
        out = this.getOutput();
        in = this.getInput();
    }
    
    public String send(String msg) throws IOException {
        out.println(msg);
        String resp = in.readLine();
        return resp;
    }
    
    public void stop() throws IOException {
        in.close();
        out.close();
        clientSocket.close();
    }
}
