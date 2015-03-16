package io.github.imdreamrunner.chat_server;

import io.github.imdreamrunner.mocket.MocketException;
import io.github.imdreamrunner.mocket.MocketServer;
import io.github.imdreamrunner.mocket.MocketServer.*;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class ChatServer {
    public static void main(String args[]) throws MocketException {
        System.out.println("Starting chat server.");
        final MocketServer server = new MocketServer(5200);

        final Map<Client, String> nicknames = new HashMap<>();

        server.on("client_connect", new ServerHandler() {
            public void handle(Client client, String content) {
                server.trigger(client, "message", createMessage("server","Who are you?"));
            }
        });

        server.on("client_disconnect", new ServerHandler() {
            public void handle(Client client, String content) {
                String name = "UNKNOWN";
                if (nicknames.containsKey(client)) {
                    name = nicknames.get(client);
                }
                System.out.println("Client " + name + " disconnect.");
                nicknames.remove(client);
            }
        });

        server.on("message", new ServerHandler() {
            public void handle(Client client, String content) {
                if (!nicknames.containsKey(client)) {
                    if (nicknames.containsValue(content) || content.contains("server")) {
                        server.trigger(client, "message",
                                "Sorry, this name is taken. Please try another one.");
                    } else {
                        server.trigger(client, "message",
                                createMessage("server", "Hi, " + content));
                        server.trigger("message",
                                createMessage("server", content + " joins the chat."));
                        nicknames.put(client, content);
                    }
                } else {
                    server.trigger("message", createMessage(nicknames.get(client), content));
                }
            }
        });

        server.start();

        System.out.println("Chat server started. Press enter to stop.");
        try {
            System.in.read();
        } catch (IOException e) {
            System.out.println("Exception: Could not block the execution.");
        }

        server.stop();
        System.out.println("Chat server stopped.");
        System.exit(0);  // Server cannot exit normally without this line.
                         // TODO: Understand why.
    }

    static String createMessage(String sender, String content) {
        return sender + ": " + content;
    }
}
