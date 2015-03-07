package io.github.imdreamrunner.mocket;

import java.util.Date;
import java.util.logging.Logger;

import com.fasterxml.jackson.databind.JsonNode;
import io.github.imdreamrunner.util.Json;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;

import static io.github.imdreamrunner.mocket.Message.MessageType;

public class MessageTest {
    private static final Logger log = Logger.getLogger(MessageTest.class.getName());

    @Test
    public void CreateMessageTest() {
        log.info("Test creating message.");
        String event = "beat";
        Message message = Message.createSystemMessage(event, null);
        assertEquals("Message type is SYSTEM", message.getType(), MessageType.SYSTEM);
        assertEquals("Message event is " + event, message.getEvent(), event);
    }

    @Test
    public void MessageToJsonTest() {
        log.info("Test message create json.");
        String event = "beat";
        String content = "test-content";
        Message message = Message.createSystemMessage(event, content);
        assertEquals("Message content", message.getContent(), content);
        String jsonString = message.toJson();
        log.info("Produced json: " + jsonString);
        JsonNode jsonNode = Json.parse(jsonString);
        assertEquals("Json type", jsonNode.get("type").textValue(), MessageType.SYSTEM.toString());
    }

    @Test
    public void MessageFromJsonTest() {
        log.info("Test message create json.");
        String event = "beat";
        String content = "test-content";
        Message originMessage = Message.createSystemMessage(event, content);
        Date originTime = originMessage.getCreateTime();
        String jsonString = originMessage.toJson();
        log.info("Testing json: " + jsonString);
        Message generatedMessage = Message.fromJson(jsonString);
        assertEquals("Create time", generatedMessage.getCreateTime(), originTime);
    }
}
