package com.codezen.plugin.context;

import com.codezen.plugin.io.MoreIO;
import com.codezen.plugin.model.PluginConfig;
import com.codezen.plugin.model.Sink;
import com.codezen.plugin.ssl.HttpUtil;
import com.codezen.plugin.status.PluginStatusWidget;
import com.google.gson.Gson;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.wm.StatusBar;
import com.intellij.openapi.wm.WindowManager;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class SessionContext {

    public static final String ENTRY_SINK = "sink";
    public static final String ENTRY_PLUGIN_CONFIG = "plugin.config";
    public static final String ENTRY_HITS = "plugin.hits";
    public static String CURRENT_USER = "context.current.user";

    public static final String ENTRY_PLUGIN_MESSAGE = "plugin.message";

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


    public SessionContext() {
        HttpUtil.disableSSL();
        Gson gson = new Gson();
        put(ENTRY_SINK, gson.fromJson(MoreIO.readFromClassPath("/config/sink.json").get(), Sink.class));
        put(ENTRY_PLUGIN_CONFIG, gson.fromJson(MoreIO.readFromClassPath("/config/codezen.json").get(), PluginConfig.class));
        put(ENTRY_HITS, new Hits.MessageIDGen());

        put(ENTRY_PLUGIN_MESSAGE, new PluginMessage());
    }

    public static SessionContext get() {
        return context;
    }

    public static void showStatusMessage(Project p, String statusMessage) {
        PluginMessage message = get().get(ENTRY_PLUGIN_MESSAGE);
        message.send(statusMessage);
        WindowManager windowManager = WindowManager.getInstance();
        StatusBar statusBar = windowManager.getStatusBar(p);
        if (statusBar != null) {
            statusBar.updateWidget(PluginStatusWidget.NAME);
        }
    }

}
