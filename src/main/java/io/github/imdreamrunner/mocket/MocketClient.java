package io.github.imdreamrunner.mocket;

import java.io.IOException;
import java.net.Socket;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

public final class MocketClient {
    private static final Logger log = Logger.getLogger(MocketClient.class.getName());

    private String host;
    private int port;
    private boolean isConnected;
    private SocketDaemon clientDaemon;
    private Map<String, List<ClientHandler>> handlers;
    private MessageHandler messageHandler;

    private MocketClient(String host, int port) {
        this.host = host;
        this.port = port;
        this.isConnected = false;
        this.handlers = new HashMap<String, List<ClientHandler>>();
        this.messageHandler = new MessageHandler();
    }

    public void connect() throws MocketException {
        try {
            Socket socket = new Socket(host, port);
            clientDaemon = new SocketDaemon(socket, this.messageHandler);
            clientDaemon.start();
        } catch (IOException e) {
            throw new MocketException(e);
        }
    }

    public void disconnect() throws MocketException {
        clientDaemon.close();
    }

    private class MessageHandler implements SocketHandler {
        @Override
        public void handleMessage(SocketDaemon socket, Message message) {
            switch (message.getType()) {
                case SYSTEM:
                    break;
                case USER:
                    if (handlers.containsKey(message.getEvent())) {
                        for (ClientHandler handler : handlers.get(message.getEvent())) {
                            handler.handle(message.getContent());
                        }
                    }
                    break;
            }
        }
    }

    public static MocketClient getInstance(String host, int port) {
        return new MocketClient(host, port);
    }

    public void on(String event, ClientHandler handler) {
        if (!handlers.containsKey(event)) {
            handlers.put(event, new ArrayList<ClientHandler>());
        }
        if (!handlers.get(event).contains(handler)) {
            handlers.get(event).add(handler);
        }
    }

    public void trigger(String event, String content) {
        log.info("Triggering event " + event + " to server.");
        Message message = Message.createUserMessage(event, content);
        clientDaemon.send(message);
    }

    public static abstract class ClientHandler {
        public abstract void handle(String content);
    }
}
