package app;

import java.util.Scanner;

public class InputHandler implements Runnable{

    private ServerTCP serverTCP;

    InputHandler(ServerTCP serverTCP) {
        this.serverTCP = serverTCP;
    }

    @Override
    public void run() {
        Scanner scanner = new Scanner(System.in);

        while(true) {
            String input = scanner.nextLine();
            if (!this.processInput(input)){
                break;
            }
        }
    }

    private boolean processInput(String input) {
        if (input.startsWith("_kill ")) {
            this.serverTCP.killUser(input.split(" ")[1]);
        } else if (input.equals("_shutdown")) {
            this.serverTCP.shutdown();
            return false;
        }
        return true;
    }
}
