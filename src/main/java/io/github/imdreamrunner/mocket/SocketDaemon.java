package io.github.imdreamrunner.mocket;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.logging.Logger;

class SocketDaemon extends Thread {
    private static final Logger log = Logger.getLogger(MocketServer.class.getName());

    private Socket socket;
    private PrintWriter out;
    private BufferedReader in;
    private MocketException exception;
    private SocketHandler handler;

    public SocketDaemon(Socket socket, SocketHandler handler) throws MocketException {
        this.socket = socket;
        this.handler = handler;
        try {
            out = new PrintWriter(socket.getOutputStream(), true);
            in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        } catch (IOException e) {
            throw new MocketException(e);
        }
    }

    @Override
    public void run(){
        try {
            String inputLine;
            while ((inputLine = in.readLine()) != null) {
                log.info("Input" + inputLine);
            }
        } catch (IOException e) {
            if (e.getMessage().equals("Socket closed")) {
                log.info("Socket has been disconnected.");
            } else {
                exception = new MocketException(e);
                log.warning("Cannot read from client: " + exception.toString());
            }
        }
    }

    public void send(Message message) {
        out.println(message.toJson());
    }

    public String getHost() {
        return socket.getInetAddress().toString();
    }

    public int getPort() {
        return socket.getPort();
    }

    public void close() throws MocketException {
        try {
            socket.close();
        } catch (IOException e) {
            throw new MocketException(e);
        }
    }
}