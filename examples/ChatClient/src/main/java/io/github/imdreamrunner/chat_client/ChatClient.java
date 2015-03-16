package io.github.imdreamrunner.chat_client;

import io.github.imdreamrunner.mocket.MocketClient;
import io.github.imdreamrunner.mocket.MocketException;
import io.github.imdreamrunner.mocket.MocketClient.*;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

public class ChatClient {
    public static void main(String args[]) throws MocketException {
        System.out.println("Starting chat client.");

        final MocketClient client = MocketClient.getInstance("127.0.0.1", 5200);

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

        System.out.println("Chat client stopped.");
    }
}
