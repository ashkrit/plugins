/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

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
import java.util.UUID;

public class SessionContext {

    public static final String ENTRY_SINK = "sink";
    public static final String ENTRY_PLUGIN_CONFIG = "plugin.config";
    public static final String ENTRY_HITS = "plugin.hits";
    public static String CURRENT_USER = "context.current.user";
    public static String CURRENT_USER_CONTEXT = "context.current.user";

    public static final String ENTRY_PLUGIN_MESSAGE = "plugin.message";

    private final Map<String, Object> data = new HashMap<>();
    private final String sessionKey;


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
        this.sessionKey = UUID.randomUUID().toString();
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

    public String sessionKey() {
        return sessionKey;
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
