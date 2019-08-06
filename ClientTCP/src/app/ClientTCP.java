package app;

import java.io.*;
import java.net.Socket;
import java.util.Scanner;

public class ClientTCP {
    private Socket clientSocket;
    private PrintWriter out;
    private BufferedReader in;
    private boolean isClientRunning = true;
    Thread clientEar;

    private BufferedReader getInput() throws IOException
    {
        return new BufferedReader(new InputStreamReader(this.clientSocket.getInputStream()));
    }

    private PrintWriter getOutput() throws IOException
    {
        return new PrintWriter(new OutputStreamWriter(this.clientSocket.getOutputStream()), true);
    }
    
    private void start(String ip) throws IOException {
    	clientSocket = new Socket(ip, 2000);
        out = this.getOutput();
        in = this.getInput();
        this.startClientListener();
        System.out.println("[INFO] Connected successfully");
    }

    private void startClientListener() {
        clientEar = new Thread(new ClientEar(this));
        clientEar.start();
    }

    private void send(String msg) {
        out.println(msg);
    }

    private void sendName(String msg) {
        out.println(msg);
    }

    private void initUserInput() {
        Scanner scanner = new Scanner(System.in);
        while(isClientRunning) {
            String input = scanner.nextLine();
            boolean isInput;
            if (!(isInput = this.processInput(input)) && clientSocket != null && !clientSocket.isClosed()) {
                this.send(input);
            } else {
                if (!isInput) {
                    System.out.println("[TYPO] Unrecognized command. type _help to start.");
                }
            }
        }
    }

    private boolean processInput(String input) {
        if (input.equals("_fetch")) {
            System.out.println("[INFO] Fetching please wait...");
            Thread bc = new Thread(new BroadcastingClient());
            bc.start();
            return true;
        } else if (input.startsWith("_connect ")) {
            this.connectToServer(input);
            return true;
        } else if (input.equals("_quit")) {
            this.send("_quit");
            return true;
        } else if (input.equals("_help")) {
            this.printHelp();
            return true;
        }
        return false;
    }

    private void printHelp() {
        System.out.println();
        System.out.println("Type _fetch to fetch all servers ip.");
        System.out.println("\t_connect <ip> <nickname>");
        System.out.println("\t_who to get a list of all connected users.");
        System.out.println("\t_quit to exit connected server and close the app.");
        System.out.println("\t_help to print this message.");
        System.out.println();
    }

    private void connectToServer(String input) {
        String[] params = input.split(" ");

        if (params.length < 3) {
            System.err.println("[TYPO] Missing param, ex: _connect <ip> <username>");
            return;
        }

        System.out.printf("[INFO] Trying connecting you to %s as %s\n", params[1], params[2]);

        try {
            this.start(params[1]);
            this.sendName(params[2]);
        } catch (IOException e) {
            System.out.printf("[WARN] Their is no running server at %s:%s\n", params[1], 2000);
            System.out.println();
        }
    }

    boolean checkResponse(String resp) {
        if (resp == null) {
            return false;
        }

    	if ("_kill".equals(resp)) {
    	    this.killConnection();
    	    return false;
        }
        return true;
    }

	private void killConnection() {
        try {
            in.close();
            out.close();
            clientSocket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        clientEar = null;
    }

	private void stop() {
        this.close();
        this.stopApp();
    }

    private void stopApp() {
        isClientRunning = false;
        System.exit(1);
    }

    Socket getClientSocket() {
        return clientSocket;
    }

    BufferedReader getSocketInput() {
        return in;
    }

    public static void main(String [] args) {
    	ClientTCP client = new ClientTCP();
        System.out.println("[INFO] TCP Client running... Type _help to start");
    	if (args.length > 2) {
    	    if (args[0].equals("_connect")) {
    	        client.connectToServer(String.join(" ", args));
            }
        }
    	client.initUserInput();
    }

    public void close() {
        try {
            in.close();
            out.close();
            clientSocket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
