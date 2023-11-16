package com.codezen.plugin.model;

import java.util.Map;

public class PluginConfig {

    public Map<String, Object> config;


    public Object value(String name) {
        return config.get(name);
    }
}
