package io.github.imdreamrunner.mocket;

import java.io.IOException;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

public class MocketServer {
    private static final Logger log = Logger.getLogger(MocketServer.class.getName());

    private static enum ServerEvent {
        SERVER_START, SERVER_STOP,
        CLIENT_CONNECT, CLIENT_DISCONNECT
    }

    private String host;
    private int port;
    private boolean isListening;
    private ServerDaemon serverDaemon;
    private Map<String, List<ServerHandler>> handlers;
    private Map<SocketDaemon, Client> clientMap = new HashMap<>();
    private ServerSocketHandler serverSocketHandler;

    public MocketServer(String host, int port) {
        this.host = host;
        this.port = port;
        this.isListening = false;
        this.handlers = new HashMap<>();
        this.serverSocketHandler = new ServerSocketHandler();
    }

    public MocketServer(int port) {
        this("0.0.0.0", port);
    }

    public void start() throws MocketException {
        if (this.isListening) {
            log.warning("The server has already been started.");
            return;
        }
        serverDaemon = new ServerDaemon();
        serverDaemon.start();
        dispatchEvent(ServerEvent.SERVER_START.toString().toLowerCase());
    }

    public void stop() throws MocketException {
        serverDaemon.close();
    }

    private void handleServerStop() {
        MocketException exception = serverDaemon.getException();
        if (exception != null) {
            log.log(Level.SEVERE, "Exception happens in server: " + exception.toString());
            exception.printStackTrace();
        }
        dispatchEvent(ServerEvent.SERVER_STOP.toString().toLowerCase());
    }

    private class ServerSocketHandler implements SocketHandler {
        @Override
        public void handleMessage(SocketDaemon socket, Message message) throws MocketException {
            log.info("Server handle message from client: " + message.toString());
            Client client;
            if (clientMap.containsKey(socket)) {
                client = clientMap.get(socket);
            } else {
                throw new MocketException("Unknown client");
            }
            if (handlers.containsKey(message.getEvent())) {
                dispatchEvent(message.getEvent(), client, message.getContent());
            }
        }

        @Override
        public void handleClose(SocketDaemon socket) {
            Client client = clientMap.get(socket);
            dispatchEvent(ServerEvent.CLIENT_DISCONNECT.toString().toLowerCase(), client);
            clientMap.remove(socket);
        }
    }

    private void dispatchEvent(String event, Client client, String content) {
        log.info("Server dispatch event " + event + " to its handler(s).");
        if (handlers.get(event) != null) {
            for (ServerHandler handler : handlers.get(event)) {
                try {
                    handler.handle(client, content);
                } catch (Exception e) {
                    log.log(Level.SEVERE, "Exception in executing the handler: " + e.toString());
                }
            }
        }
    }

    private void dispatchEvent(String event, Client client) {
        dispatchEvent(event, client, null);
    }

    private void dispatchEvent(String event, String content) {
        dispatchEvent(event, null, content);
    }

    private void dispatchEvent(String event) {
        dispatchEvent(event, null, null);
    }

    private class ServerDaemon extends Thread {
        private ServerSocket serverSocket;
        private MocketException exception;

        ServerDaemon() throws MocketException {
            try {
                InetAddress bindAddress = InetAddress.getByName(host);
                serverSocket = new ServerSocket(port, 10, bindAddress);
                isListening = true;
            } catch (IOException e) {
                throw new MocketException(e);
            }
        }

        @Override
        public void run(){
            try {
                log.info("Server is listening.");
                while (!isInterrupted()) {
                    Socket socket = serverSocket.accept();
                    log.info("New client connected.");
                    SocketDaemon clientThread = new SocketDaemon(socket, serverSocketHandler);
                    Client client = new Client(clientThread);
                    clientMap.put(clientThread, client);
                    clientThread.start();
                    dispatchEvent(ServerEvent.CLIENT_CONNECT.toString().toLowerCase(), client);
                }
                for (SocketDaemon clientThread : clientMap.keySet()) {
                    clientThread.close();
                }
                if (!serverSocket.isClosed()) {
                    // TODO: Verify if this is necessary.
                    serverSocket.close();
                }
            } catch (SocketException e) {
                log.info("Server has stopped listening.");
            } catch (IOException e) {
                this.exception = new MocketException(e);
            } catch (MocketException e) {
                this.exception = e;
            }
            handleServerStop();
            log.info("ServerDaemon really finished.");
        }

        public void close() throws MocketException {
            try {
                serverSocket.close();
            } catch (IOException e) {
                throw new MocketException(e);
            }
        }

        public MocketException getException() {
            return exception;
        }
    }

    public void on(String event, ServerHandler handler) {
        if (!handlers.containsKey(event)) {
            handlers.put(event, new ArrayList<ServerHandler>());
        }
        if (!handlers.get(event).contains(handler)) {
            handlers.get(event).add(handler);
        }
    }

    public void trigger(String event, String content) {
        log.info("Triggering event " + event + " to all clients");
        for (Client client : clientMap.values()) {
            trigger(client, event, content);
        }
    }

    public void trigger(Client client, String event, String content) {
        log.info("Triggering event " + event + " to " + client.toString());
        client.trigger(event, content);
    }

    public static class Client {
        private SocketDaemon socket;

        Client(SocketDaemon socket) {
            this.socket = socket;
        }

        public String getHost() {
            return socket.getHost();
        }

        public int getPort() {
            return socket.getPort();
        }

        public void trigger(String event, String content) {
            Message message = Message.createUserMessage(event, content);
            socket.send(message);
        }

        @Override
        public boolean equals(Object object) {
            if (!(object instanceof Client)) {
                return false;
            }
            Client temp = (Client) object;
            return this.socket == temp.socket;
        }

        @Override
        public String toString() {
            return "<Client " + getHost() + ":" + getPort() + ">";
        }
    }

    public static abstract class ServerHandler {
        public abstract void handle(Client client, String content) throws Exception;
    }
}
