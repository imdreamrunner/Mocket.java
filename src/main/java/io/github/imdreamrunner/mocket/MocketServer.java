package io.github.imdreamrunner.mocket;

import java.io.IOException;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

public class MocketServer {
    private static final Logger log = Logger.getLogger(MocketServer.class.getName());

    private String host;
    private int port;
    private boolean isListening;
    private ServerDaemon serverDaemon;
    private Map<String, List<ServerHandler>> handlers;
    private Map<SocketDaemon, Client> clientMap = new HashMap<SocketDaemon, Client>();
    private MessageHandler messageHandler;

    public MocketServer(String host, int port) {
        this.host = host;
        this.port = port;
        this.isListening = false;
        this.handlers = new HashMap<String, List<ServerHandler>>();
        this.messageHandler = new MessageHandler();
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
    }

    public void stop() throws MocketException {
        serverDaemon.close();;
    }

    private void handleServerStop() {
        MocketException exception = serverDaemon.getException();
        if (exception != null) {
            log.log(Level.SEVERE, "Exception happens in server");
            log.log(Level.SEVERE, exception.toString());
        }
    }

    private class MessageHandler implements SocketHandler {
        @Override
        public void handleMessage(SocketDaemon socket, Message message) {
            Client client;
            if (clientMap.containsKey(socket)) {
                client = clientMap.get(socket);
            } else {
                client = new Client(socket);
                clientMap.put(socket, client);
            }
            if (handlers.containsKey(message.getEvent())) {
                for (ServerHandler handler : handlers.get(message.getEvent())) {
                    handler.handle(client, message.getContent());
                }
            }
        }
    }

    private class ServerDaemon extends Thread {
        private ServerSocket serverSocket;
        private MocketException exception;

        ServerDaemon() throws MocketException {
            try {
                InetAddress bindAddress = InetAddress.getByName(host);
                serverSocket = new ServerSocket(port, 10, bindAddress);
                isListening = true;
            } catch (UnknownHostException e) {
                throw new MocketException(e);
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
                    SocketDaemon clientThread = new SocketDaemon(socket, messageHandler);
                    clientThread.start();
                }
                if (!serverSocket.isClosed()) {
                    serverSocket.close();
                }
            } catch (IOException e) {
                if (e.getMessage().equals("Socket closed")) {
                    log.info("Server has stopped listening.");
                } else {
                    this.exception = new MocketException(e);
                }
            } catch (MocketException e) {
                this.exception = e;
            }
            handleServerStop();
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

    public void trigger(String event, String message) {

    }

    public void trigger(Client client, String event, String message) {

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
        public abstract void handle(Client client, String content);
    }
}
