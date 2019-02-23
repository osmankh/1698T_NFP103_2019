package app;

import java.io.*;
import java.net.ServerSocket;
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

    public static void main(String[] args) throws IOException
    {
        ServerSocket ecoute;
        ecoute = new ServerSocket(2000);
        System.out.printf("L'adresse de la socket d'ecoute est %s\n",
                ecoute.getLocalSocketAddress());
        while(true)
        {
            Socket serviceSocket = ecoute.accept();
            System.out.printf("L'adresse de la socket cliente (remote) est %s\n",
                    serviceSocket.getRemoteSocketAddress());
            BufferedReader ir = getInput(serviceSocket);
            PrintWriter reply = getOutput(serviceSocket);
            String line;
            line = ir.readLine();
            System.out.printf("Recu %s du Client\n", line);
            reply.printf("Reponse Serveur %s\n", line);
            reply.flush();
            serviceSocket.close();
        }
    }
}
