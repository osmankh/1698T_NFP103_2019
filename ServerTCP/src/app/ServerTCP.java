package app;

import java.io.*;
import java.net.Socket;

public class ServerTCP {

    private static BufferedReader getInput(Socket p) throws IOException
    {
        return new BufferedReader(new InputStreamReader(p.getInputStream()));
    }

    private static PrintWriter getOutput(Socket p) throws IOException
    {
        return new PrintWriter(new OutputStreamWriter(p.getOutputStream()));
    }

    public static void main(String[] args) {
	// write your code here
    }
}
