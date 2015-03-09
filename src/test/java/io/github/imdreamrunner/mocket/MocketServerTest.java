package io.github.imdreamrunner.mocket;

import org.junit.Test;

import java.util.logging.Logger;
import io.github.imdreamrunner.mocket.MocketServer.*;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

public class MocketServerTest {
    private static final Logger log = Logger.getLogger(MocketServerTest.class.getName());
    MocketServer server;
    MocketClient client;

    int expectMessage = 0;
    int receivedMessage = 0;

    static int serverPort = 5000;

    @Test
    public void serverTest() throws InterruptedException {
        expectMessage = 4;

        log.info("Starting test server at port " + serverPort + ".");
        server = new MocketServer(serverPort);

        server.on("server_start", new ServerHandler() {
            public void handle(Client client, String content) {
                log.info("Server started.");
                receivedMessage += 1;
            }
        });

        server.on("client_connect", new ServerHandler() {
            public void handle(Client client, String content) {
                log.info("Client " + client.toString() + " connected.");
                receivedMessage += 1;
            }
        });

        server.on("server_stop", new ServerHandler() {
            public void handle(Client client, String content) {
                log.info("Server stopped.");
                receivedMessage += 1;
            }
        });

        server.on("client_disconnect", new ServerHandler() {
            public void handle(Client client, String content) {
                log.info("Client " + client.toString() + " disconnected.");
                receivedMessage += 1;
            }
        });

        try {
            server.start();
        } catch (MocketException e) {
            fail("Start server with exception: " + e.toString());
        }
        log.info("Creating test client.");
        client = MocketClient.getInstance("127.0.0.1", serverPort);
        try {
            client.connect();
        } catch (MocketException e) {
            fail("Start client with exception: " + e.toString());
        }

        try {
            log.info("Stopping test server.");
            server.stop();
            log.info("Deleting test client.");
            client.disconnect();
        } catch (MocketException e) {
            fail("Exception: " + e.toString());
        }

        int sleepCount = 0;
        while (receivedMessage < expectMessage && sleepCount < 10) {
            Thread.sleep(200);
            sleepCount++;
        }

        assertEquals("Number of message received", expectMessage, receivedMessage);
    }

}
