package com.codezen.plugin.context;

public class PluginMessage {
    public String message;

    public void send(String message) {
        this.message = message;
    }

    public String message() {
        return message;
    }
}
