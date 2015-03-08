package io.github.imdreamrunner.mocket;

import java.util.Date;
import java.util.logging.Logger;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import io.github.imdreamrunner.util.Json;

class Message {
    private static final Logger log = Logger.getLogger(Message.class.getName());

    public enum MessageType {
        SYSTEM, USER
    }

    private MessageType type;
    private String event;
    private String content;
    private Date createTime;

    private Message(MessageType type, String event, String content, Date createTime) {
        this.type = type;
        this.event = event;
        this.content = content;
        this.createTime = createTime;
    }

    protected static Message createSystemMessage(String event, String content) {
        return new Message(MessageType.SYSTEM, event, content, new Date());
    }

    protected static Message createSystemMessage(String event, String content, Date createTime) {
        return new Message(MessageType.SYSTEM, event, content, createTime);
    }

    protected static Message createUserMessage(String event, String content) {
        return new Message(MessageType.USER, event, content, new Date());
    }

    protected static Message createUserMessage(String event, String content, Date createTime) {
        return new Message(MessageType.USER, event, content, createTime);
    }

    public MessageType getType() {
        return type;
    }

    public String getEvent() {
        return event;
    }

    public String getContent() {
        return content;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public String toJson() {
        ObjectNode json = Json.newObject();
        json.put("type", type.toString());
        json.put("event", event);
        json.put("content", content);
        json.put("createTime", createTime.getTime());
        return Json.stringify(json);
    }

    public static Message fromJson(String jsonString) {
        JsonNode json = Json.parse(jsonString);
        MessageType type = MessageType.valueOf(json.get("type").asText());
        String event = json.get("event").asText();
        String content = json.get("content").asText();
        Date createTime = new Date(json.get("createTime").asLong());
        return new Message(type, event, content, createTime);
    }

    @Override
    public String toString() {
        return "Mocket Message: " + toJson();
    }
}
