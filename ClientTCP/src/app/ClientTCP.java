package app;

import java.io.*;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.Scanner;

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
        return new PrintWriter(new OutputStreamWriter(this.clientSocket.getOutputStream()), true);
    }
    
    public void start(String ip, int port) throws UnknownHostException, IOException {
    	clientSocket = new Socket(ip, port);
        out = this.getOutput();
        in = this.getInput();
        
        Scanner scanner = new Scanner(System.in);
        while(!clientSocket.isClosed()) {
        	String input = scanner.nextLine();
        	this.send(input);
        }
        scanner.close();
    }
    
    public void send(String msg) throws IOException {
        out.println(msg);
        String resp = in.readLine();
        this.checkResponse(resp);
    }
    
    private void checkResponse(String resp) {
		System.out.println(resp);
    	if ("bye".equals(resp)) {
			try {
				this.stop();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	public void stop() throws IOException {
        in.close();
        out.close();
        clientSocket.close();
    }
    
    public static void main(String [] args) throws UnknownHostException, IOException {
    	ClientTCP client = new ClientTCP();
    	client.start("127.0.0.1", 2000);
    }
}
