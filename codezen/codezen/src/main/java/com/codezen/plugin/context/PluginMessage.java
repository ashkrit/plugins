package com.codezen.plugin.context;

import java.util.Map;
import java.util.SortedMap;
import java.util.concurrent.ConcurrentSkipListMap;

public class PluginMessage {
    public String message;
    public SortedMap<Integer, ServerMessage> messages = new ConcurrentSkipListMap<>();

    public void send(String message) {
        this.message = message;
    }

    public String message() {
        return message;
    }

    public void append(int id, ServerMessage message) {
        messages.put(id, message);
    }

    public ServerMessage lastMessage() {
        return messages.get(messages.lastKey());
    }

    public int lastMessageId() {
        return messages.isEmpty() ? 0 : messages.lastKey();
    }

    public static class ServerMessage {
        public String type;
        public Map<String, Object> data;

    }
}
