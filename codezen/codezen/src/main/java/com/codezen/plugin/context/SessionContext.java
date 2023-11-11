package com.codezen.plugin.context;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class SessionContext {

    public static String CURRENT_USER = "context.current.user";

    private final Map<String, Object> data = new HashMap<>();

    public <K> void put(String key, K value) {
        data.put(key, value);
    }

    public <K> K get(String key) {
        return (K) data.get(key);
    }

    public <K> K remove(String key) {
        return (K) data.remove(key);
    }

    public Set<String> keys() {
        return data.keySet();
    }

    public static final SessionContext context = new SessionContext();

    public static SessionContext get() {
        return context;
    }

}
