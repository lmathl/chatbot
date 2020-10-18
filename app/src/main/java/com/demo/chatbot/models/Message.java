package com.demo.chatbot.models;

import java.io.Serializable;

public class Message implements Serializable {
    String id, message, type;
    boolean isSelf;

    public Message(String message, String type, boolean isSelf) {
        this.message = message;
        this.type = type;
        this.isSelf = isSelf;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public boolean getIsSelf() {
        return isSelf;
    }

    public void setIsSelf(boolean isSelf) {
        this.isSelf = isSelf;
    }
}