package io.github.imdreamrunner.mocket;

interface SocketHandler {
    void handleMessage(SocketDaemon socket, Message message) throws MocketException;
}
