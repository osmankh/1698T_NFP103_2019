package app;

import java.io.*;
import java.net.*;

public class ServerTCP {
    private ServerSocket serverSocket;
    private Socket clientSocket;
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

    private void start(int port) throws IOException {
        serverSocket = new ServerSocket(port);
        this.printServerAddress();

        clientSocket = serverSocket.accept();
        this.printClientAddress();

        out = this.getOutput(clientSocket);
        in = this.getInput(clientSocket);
        String message;
        
        while ((message = in.readLine()) != null) {
            System.out.println("Receiving data from client");
            if(!this.checkMessage(message)) {
            	this.stop();
            	break;
            }
        }
    }

    public void stop() throws IOException {
        in.close();
        out.close();
        clientSocket.close();
        serverSocket.close();
    }

    private void printServerAddress() {
        System.out.printf("L'adresse de la socket d'serverSocket est %s\n",
                serverSocket.getLocalSocketAddress());
    }

    private void printClientAddress() {
        System.out.printf("L'adresse de la socket client (remote) est %s\n",
                clientSocket.getRemoteSocketAddress());
    }
    
    private void quitClient() {
    	out.println("bye");
    	System.out.printf("Client with address %s disconnected!", clientSocket.getRemoteSocketAddress());
    	try {
			this.stop();
		} catch (IOException e) {
			e.printStackTrace();
		}
    }

    private boolean checkMessage(String message) {
    	if ("hello server".equals(message)) {
            out.println("hello client");
        }else if("_quit".equals(message)) {
        	this.quitClient();
        	return false;
        }
        else {
            out.printf("unrecognised message %s", message);
        }
        
        return true;
    }

    public static void main(String[] args) throws IOException
    {
        ServerTCP server = new ServerTCP();
        Thread bs = new Thread(new BroadcastingServer());
        bs.start();
        server.start(2000);
    }
}
