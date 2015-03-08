package io.github.imdreamrunner.mocket;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.util.logging.Logger;
import io.github.imdreamrunner.mocket.MocketServer.*;
import io.github.imdreamrunner.mocket.MocketClient.*;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

public class NetworkTest {
    private static final Logger log = Logger.getLogger(MocketServerTest.class.getName());
    MocketServer server;
    MocketClient client;

    int expectMessage = 0;
    int receivedMessage = 0;

    static int serverPort = 5000;

    @Before
    public void startServer() {
        log.info("Starting test server at port " + serverPort + ".");
        server = new MocketServer(serverPort);
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
    }

    @After
    public void stopServer() {
        try {
            log.info("Stopping test server.");
            server.stop();
            log.info("Deleting test client.");
            client.disconnect();
            assertEquals("Receive message number", expectMessage, receivedMessage);
        } catch (MocketException e) {
            fail("Exception: " + e.toString());
        }
    }

    @Test
    public void serverSendMessageTest() {

    }

    @Test
    public void clientSendingTest() throws InterruptedException {
        expectMessage = 1;
        final String event = "testEvent";
        final String message = "Test Message";
        server.on(event, new ServerHandler() {
            public void handle(Client client, String content) {
                log.info("Receive message " + content + " from " + client.toString());
                receivedMessage += 1;
                assertEquals("Message content", message, content);
            }
        });
        client.trigger(event, message);
        Thread.sleep(200);
    }

    @Test
    public void serverSendingTest() throws InterruptedException {
        expectMessage = 1;
        final String event = "testEvent";
        final String message = "Test Message";
        client.on(event, new ClientHandler() {
            @Override
            public void handle(String content) {
                log.info("Receive message " + content + " from server.");
                receivedMessage += 1;
                assertEquals("Message content", message, content);
            }
        });
        server.trigger(event, message);
        Thread.sleep(200);
    }
}
