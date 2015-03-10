package io.github.imdreamrunner.mocket;

import org.junit.Test;
import java.util.logging.Logger;

import io.github.imdreamrunner.mocket.MocketServer.*;
import io.github.imdreamrunner.mocket.MocketClient.*;

public class MocketClientTest {
    private static final Logger log = Logger.getLogger(MocketServerTest.class.getName());

    private static int serverPort = 5100;
    private static MocketServer testServer;
    private static MocketClient testClient;

    @Test
    public void clientStartAndStop() throws MocketException, InterruptedException {
        testServer = new MocketServer(serverPort);
        testServer.on("server_start", afterServerStart);
        testServer.on("server_stop", afterServerStop);
        testServer.on("client_connect", new ServerHandler() {
            public void handle(MocketServer.Client client, String content) {
                log.info("Client " + client.toString() + " connected.");
            }
        });

        testClient = MocketClient.getInstance("127.0.0.1", serverPort);
        testClient.on("server_connect", afterClientConnect);

        testServer.start();
        Thread.sleep(1000);
    }

    public ServerHandler afterServerStart = new ServerHandler() {
        public void handle(Client _, String content) throws Exception {
            log.info("Server start handler called.");
            testClient.connect();
        }
    };

    public ServerHandler afterServerStop = new ServerHandler() {
        public void handle(Client _, String content) throws Exception {
            log.info("Server stop handler called.");
        }
    };

    public ClientHandler afterClientConnect = new ClientHandler() {
        public void handle(String content) throws Exception {
            log.info("Client connected handler called.");
            testServer.stop();
        }
    };
}
