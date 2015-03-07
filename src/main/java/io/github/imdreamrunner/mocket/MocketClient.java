package io.github.imdreamrunner.mocket;

public final class MocketClient {
    private MocketClient(String host, int port) {

    }

    public static MocketClient getInstance(String host, int port) {
        return new MocketClient(host, port);
    }
}
