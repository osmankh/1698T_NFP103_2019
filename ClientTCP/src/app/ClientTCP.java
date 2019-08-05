package app;

import java.io.*;
import java.net.InetAddress;
import java.net.Socket;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.Scanner;

public class ClientTCP {
    private Socket clientSocket;
    private PrintWriter out;
    private BufferedReader in;
    private boolean isClientRunning = true;

    private BufferedReader getInput() throws IOException
    {
        return new BufferedReader(new InputStreamReader(this.clientSocket.getInputStream()));
    }

    private PrintWriter getOutput() throws IOException
    {
        return new PrintWriter(new OutputStreamWriter(this.clientSocket.getOutputStream()), true);
    }
    
    public void start(String ip, int port) throws IOException {
    	clientSocket = new Socket(ip, port);
        out = this.getOutput();
        in = this.getInput();
        this.startClientListener();
        System.out.println("Connected successfully");
    }

    private void startClientListener() {
        Thread clientEar = new Thread(new ClientEar(this));
        clientEar.start();
    }

    public void send(String msg) throws IOException {
        out.println(msg);
    }

    public void sendName(String msg) {
        out.println(msg);
    }

    public void initUserInput () throws IOException {
        Scanner scanner = new Scanner(System.in);
        System.out.println("TCP Client running...");
        System.out.println("Type _help to start");
        while(isClientRunning) {
            String input = scanner.nextLine();
            if (clientSocket != null && !clientSocket.isClosed()) {
                this.send(input);
            }
            this.processInput(input);
        }
    }

    private void processInput(String input) {
        if (input.startsWith("_fetch")) {
            System.out.println("Fetching please wait...");
            Thread bc = new Thread(new BroadcastingClient());
            bc.start();
        } else if (input.startsWith("_connect ")) {
            this.connectToServer(input);
        } else if (input.equals("_help")) {
            this.printHelp();
        }
    }

    private void printHelp() {
        System.out.println("Type _fetch to fetch all servers ip.");
        System.out.println("_connect <ip> <nickname>");
        System.out.println("_help to print this message");
    }

    private void connectToServer(String input) {
        String[] params = input.split(" ");

        if (params.length < 3) {
            System.err.println("Missing param, ex: _connect <ip> <username>");
            return;
        }

        System.out.printf("Trying connecting you to %s as %s\n", params[1], params[2]);

        try {
            this.start(params[1], 2000);
            this.sendName(params[2]);
        } catch (IOException e) {
            System.out.printf("[WARN] Their is no running server at %s:%s\n", params[1], 2000);
            System.out.println();
        }
    }

    void checkResponse(String resp) {
    	if ("_quit".equals(resp)) {
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
        isClientRunning = false;
        clientSocket.close();
        System.exit(1);
    }

    public Socket getClientSocket() {
        return clientSocket;
    }

    public BufferedReader getSocketInput() {
        return in;
    }

    public static void main(String [] args) throws IOException {
    	ClientTCP client = new ClientTCP();
    	if (args.length > 2) {
    	    if (args[0].equals("_connect")) {
    	        client.connectToServer(String.join(" ", args));
            }
        }
    	client.initUserInput();
    }
}
