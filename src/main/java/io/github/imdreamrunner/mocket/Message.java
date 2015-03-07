package io.github.imdreamrunner.mocket;

class Message {
    private String event;
    private String content;

    protected Message(String event, String content) {

    }

    public String getEvent() {
        return event;
    }

    public String getContent() {
        return content;
    }

    public String toJson() {
        return null;
    }

    @Override
    public String toString() {
        return "Mocket Message: " + toJson();
    }
}
