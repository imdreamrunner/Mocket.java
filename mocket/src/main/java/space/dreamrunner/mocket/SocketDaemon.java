package space.dreamrunner.mocket;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.SocketException;
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
            log.info("Socket has been connected.");
        } catch (IOException e) {
            throw new MocketException(e);
        }
    }

    @Override
    public void run(){
        try {
            String inputLine;
            while ((inputLine = in.readLine()) != null) {
                log.info("Receiving " + inputLine);
                Message message = Message.fromJson(inputLine);
                handler.handleMessage(this, message);
            }
        } catch (SocketException e) {
            log.info("Socket has been disconnected.");
        } catch (IOException e) {
            exception = new MocketException(e);
            log.warning("Cannot read from client: " + exception.toString());
        } catch (MocketException e) {
            exception = e;
            log.warning("Exception reading from client: " + exception.toString());
        }
        handler.handleClose(this);
    }

    public void send(Message message) {
        log.info("Sending message " + message.toString());
        out.println(message.toJson());
    }

    public String getHost() {
        return socket.getInetAddress().getHostAddress();
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
