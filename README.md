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
MocketServer server = new MocketServer(5000); 
// Replace 5000 by the port you want to listen to.
server.start();
// Do stuff.
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

#### Send message to clients

```java
// You can broadcast message to all clients
server.trigger("some-event", "Message content.");
// Or send to a specific one.
server.trigger(client, "some-event", "Message content.");
```

### Mocket Client

#### Things to import

```java
import io.github.imdreamrunner.mocket.MocketClient;
import io.github.imdreamrunner.mocket.MocketClient.*;
```

#### Initialize the client

```java
MocketClient client = MocketClient.getInstance("127.0.0.1", 5000);
// Replace "127.0.0.1" with server's host name or IP address,
// and 5000 with server's port.
client.connect();
// Do stuff.
client.disconnect();
```

#### Handle message from server

```java
client.on(event, new ClientHandler() {
    public void handle(String content) {
        log.info("Receive message " + content + " from server.");
    }
});
```

#### Send message to server

```java
client.trigger("some-event", "Some message.");
```

## License

```
The MIT License (MIT)

Copyright (c) 2015 Xinzi Zhou

Permission is hereby granted, free of charge, to any person obtaining a copy
of this software and associated documentation files (the "Software"), to deal
in the Software without restriction, including without limitation the rights
to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
copies of the Software, and to permit persons to whom the Software is
furnished to do so, subject to the following conditions:

The above copyright notice and this permission notice shall be included in all
copies or substantial portions of the Software.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
SOFTWARE.
```
