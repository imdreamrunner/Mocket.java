# Mocket.java

The Java implementation of Mocket, an elegant cross-language socket library.

Using Mocket, you can easily create event-based socket connections among programs
developed in different programming languages.

## Usage

### Mocket Server

#### Things to import

```java
import io.github.imdreamrunner.mocket.MocketServer;
import io.github.imdreamrunner.mocket.MocketServer.*;
```

#### Initialize the server

```java
MocketServer server = new MocketServer(5000);  // Replace 5000 by the port you want to listen to.
server.start();
// do stuff
server.stop();
```

#### Handle message from client

```java
server.on("some-event", new ServerHandler() {
    public void handle(Client client, String content) {
        System.out.println("Receive message " + content + " from " + client.toString());
    }
});
```

#### Send message from client

```java
// You can broadcast message to all clients
server.trigger("some-event", "Message content.");
// Or send to a specific one.
server.trigger(client, "some-event", "Message content.");
```

