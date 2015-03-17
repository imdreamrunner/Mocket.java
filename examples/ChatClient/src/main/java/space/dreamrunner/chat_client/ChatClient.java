package space.dreamrunner.chat_client;

import space.dreamrunner.mocket.MocketClient;
import space.dreamrunner.mocket.MocketClient.*;
import space.dreamrunner.mocket.MocketException;

import java.util.Scanner;

public class ChatClient {
    public static void main(String args[]) throws MocketException {
        if (args.length != 2) {
            System.out.println("Please start this program with <host> <port>");
            System.exit(1);
        }

        String host = args[0];
        int port = Integer.parseInt(args[1]);

        System.out.println("Client connecting to " + host + ":" + port + ".");

        final MocketClient client = MocketClient.getInstance(host, port);

        client.on("message", new ClientHandler() {
            public void handle(String content) {
                System.out.println(content);
            }
        });

        client.on("server_connect", new ClientHandler() {
            public void handle(String content) {
                System.out.println("Server connected.");
                Scanner scanner = new Scanner(System.in);
                do {
                    String message = scanner.nextLine();
                    if (message.equals("exit")) {
                        break;
                    }
                    client.trigger("message", message);
                } while (true);
                System.out.println("Bye.");
                System.exit(0);
            }
        });

        client.on("server_disconnect", new ClientHandler() {
            public void handle(String content) {
                System.out.println("You have been disconnected from the server.");
                System.exit(0);
            }
        });

        client.connect();
    }
}
