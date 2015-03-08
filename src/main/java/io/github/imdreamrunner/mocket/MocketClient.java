package io.github.imdreamrunner.mocket;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.logging.Logger;

public final class MocketClient {
    private static final Logger log = Logger.getLogger(MocketClient.class.getName());

    private String host;
    private int port;
    private boolean isConnected;
    private SocketDaemon clientDaemon;
    private MessageHandler messageHandler;

    private MocketClient(String host, int port) {
        this.host = host;
        this.port = port;
        this.isConnected = false;
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
        }
    }

    public static MocketClient getInstance(String host, int port) {
        return new MocketClient(host, port);
    }
}
