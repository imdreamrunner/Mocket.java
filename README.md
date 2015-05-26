# Mocket.java [![Build Status](https://travis-ci.org/imdreamrunner/Mocket.java.svg?branch=master)](https://travis-ci.org/imdreamrunner/Mocket.java)

The Java implementation of Mocket, an elegant cross-language socket library.

Using Mocket, you can easily create event-based socket connections among programs
developed in different programming languages.

## Mocket 101

### Sending message using Mocket

To send message using Mocket, you need to set up a server and several clients. The server will maintain the connections
to all clients, while the clients do not communicate between each other directly.

Mocket is event-based. Every message sent between server and client will be entitled an event name and a content.
Except for user defined messages, there are some system built-in ones for special purposes. They are listed in related
sections below.

### Add Mocket as dependency

#### Using a project management / build tool

It will be easy if you are using a project management or build tool for your project. We currently hosting a Maven 
repository at `http://maven.dreamrunner.space`. Please edit your configuration file according to the sample below.

##### Maven

`pom.xml`

```
<project>
    ...
    <repositories>
        ...
        <repository>
            <id>mocket-repo</id>
            <name>mocket repo</name>
            <url>http://maven.dreamrunner.space</url>
        </repository>
    </repositories>
    ...
    <dependencies>
        ...
        <dependency>
            <groupId>space.dreamrunner</groupId>
            <artifactId>mocket</artifactId>
            <version>1.0-SNAPSHOT</version>
        </dependency>
    </dependencies>
</project>
```

##### Gradle

`build.gradle`

```
...
repositories {
    ...
    maven {
        url "http://maven.dreamrunner.space"
    }
}
dependencies {
    ...
    compile group: 'space.dreamrunner', name: 'mocket', version:'1.0-SNAPSHOT'
}
```

#### Using the jar

To be added.

### Mocket Server

#### Things to import

```java
import space.dreamrunner.mocket.MocketServer;
import space.dreamrunner.mocket.MocketServer.*;
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

#### Built-in messages for server

* `server_start`: Trigger when the server has started listening to the port.
* `server_stop`: Trigger when the server has stopped listening to the port.
* `client_connect`: Trigger when there is a new client connected to the server.
* `client_disconnect`: Trigger when a client is disconnected from the server.

### Mocket Client

#### Things to import

```java
import space.dreamrunner.mocket.MocketClient;
import space.dreamrunner.mocket.MocketClient.*;
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
client.on("some-event", new ClientHandler() {
    public void handle(String content) {
        log.info("Receive message " + content + " from server.");
    }
});
```

#### Send message to server

```java
client.trigger("some-event", "Some message.");
```

#### Built-in events for client

* `server_connect`: Trigger when the clients has been connected to the server.
* `server_disconnect`: Trigger when the server has been disconnected from the server.

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
