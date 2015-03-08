package io.github.imdreamrunner.mocket;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.util.logging.Logger;
import io.github.imdreamrunner.mocket.MocketServer.*;

import static org.junit.Assert.fail;

public class MocketServerTest {
    private static final Logger log = Logger.getLogger(MocketServerTest.class.getName());
    MocketServer server;
    MocketClient client;

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
        } catch (MocketException e) {
            fail("Exception: " + e.toString());
        }
    }

    @Test
    public void serverSendMessageTest() {
        server.trigger("test", "content");
    }

    @Test
    public void createHandlerTest() {
        ServerHandler handler = new ServerHandler() {
            @Override
            public void handle(Client client, String content) {
                log.info("Receive message " + content + " from " + client.toString());
            }
        };
        server.on("test", handler);
    }
}
