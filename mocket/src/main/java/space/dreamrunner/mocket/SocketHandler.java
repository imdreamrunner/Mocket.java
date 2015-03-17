package space.dreamrunner.mocket;

interface SocketHandler {
    void handleMessage(SocketDaemon socket, Message message) throws MocketException;
    void handleClose(SocketDaemon socket);
}
