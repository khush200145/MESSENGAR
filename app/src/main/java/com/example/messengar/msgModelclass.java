package com.example.messengar;

public class msgModelclass {
    String message;
    String senderId;
    long timestamp;

    public msgModelclass() {
    }

    public msgModelclass(String message, String senderId, long timestamp) {
        this.message = message;
        this.senderId=senderId;
        this.timestamp=timestamp;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getSenderId() {
        return senderId;
    }

    public void setSenderId(String senderId) {
        this.senderId = senderId;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }
}
