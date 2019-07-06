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
    
    public void start(String ip, int port) throws UnknownHostException, IOException {
    	clientSocket = new Socket(ip, port);
        out = this.getOutput();
        in = this.getInput();
        System.out.println("Connected successfully");
    }
    
    public void send(String msg) throws IOException {
        out.println(msg);
        System.out.println("after sending message to server");
        String resp = in.readLine();
        System.out.println("after reading response from the server");
        this.checkResponse(resp);
    }

    public void initUserInput () throws IOException {
        Scanner scanner = new Scanner(System.in);
        System.out.println("TCP Client running...");
        System.out.println("Type _help to start");
        while(isClientRunning) {
            String input = scanner.nextLine();
            System.out.println(input);

            if (clientSocket != null && !clientSocket.isClosed()) {
                this.send(input);
            }
            System.out.println("After sending to client");

            this.processInput(input);

            System.out.println(" after processing client message");

        }
    }

    private void processInput(String input) throws SocketException {
        System.out.println("In processing client message");

        if (input.startsWith("_fetch")) {
            System.out.println("Fetching please wait...");
            Thread bc = new Thread(new BroadcastingClient());
            bc.start();
            System.out.println("Fetching end.");
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
        } catch (IOException e) {
            e.printStackTrace();
        }
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
        isClientRunning = false;
        clientSocket.close();
    }
    
    public static void main(String [] args) throws UnknownHostException, IOException {
    	ClientTCP client = new ClientTCP();
    	client.initUserInput();
    }
}
