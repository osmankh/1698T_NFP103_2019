package app;

import java.io.*;
import java.net.Socket;

public class ClientTCP {

    private static BufferedReader getInput(Socket p) throws IOException
    {
        return new BufferedReader(new InputStreamReader(p.getInputStream()));
    }

    private static PrintWriter getOutput(Socket p) throws IOException
    {
        return new PrintWriter(new OutputStreamWriter(p.getOutputStream()));
    }

    public static void main(String[] args) throws IOException
    {
        Socket l = new Socket("localhost", 2000);
        System.out.println(l.getLocalSocketAddress());
        BufferedReader ir = getInput(l);
        PrintWriter reply = getOutput(l);
        reply.printf("Bonjour\n"); reply.flush();
        System.out.println(ir.readLine());
    }
}
