package space.dreamrunner.mocket;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.util.HashMap;
import java.util.Map;
import java.util.logging.Logger;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

public class NetworkTest {
    private static final Logger log = Logger.getLogger(MocketServerTest.class.getName());
    MocketServer server;
    MocketClient client;

    int expectMessage = 0;
    int receivedMessage = 0;

    static int serverPort = 5102;

    @Before
    public void startServer() {
        Map<String, String> testConfig =  new HashMap<String, String>() {
            {
                put("log", "true");
            }
        };
        log.info("Starting test server at port " + serverPort + ".");
        server = new MocketServer(serverPort, testConfig);
        log.info("Creating test client.");
        client = MocketClient.getInstance("127.0.0.1", serverPort, testConfig);
    }

    @After
    public void stopServer() throws InterruptedException {
        try {
            server.start();
        } catch (MocketException e) {
            fail("Start server with exception: " + e.toString());
        }
        try {
            client.connect();
        } catch (MocketException e) {
            fail("Start client with exception: " + e.toString());
        }
        int sleepCount = 0;
        while (receivedMessage < expectMessage && sleepCount < 20) {
            Thread.sleep(200);
            sleepCount++;
        }
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
    public void clientSendingTest() {
        expectMessage = 1;
        final String event = "testEvent";
        final String message = "Test Message";
        server.on(event, new MocketServer.ServerHandler() {
            public void handle(MocketServer.Client client, String content) {
                log.info("Receive message " + content + " from " + client.toString());
                receivedMessage += 1;
                assertEquals("Message content", message, content);
            }
        });
        client.on("server_connect", new MocketClient.ClientHandler() {
            public void handle(String content) {
                client.trigger(event, message);
            }
        });
    }

    @Test
    public void serverSendingTest() throws InterruptedException {
        expectMessage = 1;
        final String event = "testEvent";
        final String message = "Test Message";
        client.on(event, new MocketClient.ClientHandler() {
            @Override
            public void handle(String content) {
                log.info("Receive message " + content + " from server.");
                receivedMessage += 1;
                assertEquals("Message content", message, content);
            }
        });
        server.on("client_connect", new MocketServer.ServerHandler() {
            public void handle(MocketServer.Client client, String content) {
                server.trigger(client, event, message);
            }
        });
    }
}
