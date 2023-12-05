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

package com.codezen.plugin;

import com.codezen.plugin.context.PluginMessage;
import com.codezen.plugin.context.SessionContext;
import com.codezen.plugin.model.PluginConfig;
import com.codezen.plugin.model.Sink;
import com.codezen.plugin.sink.SinkConsumer;
import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.startup.StartupActivity;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;

import static com.codezen.plugin.context.SessionContext.*;
import static java.util.concurrent.TimeUnit.SECONDS;

public class PluginStartup implements StartupActivity.Background {

    private static final Logger LOG = Logger.getInstance(PluginStartup.class);
    private final ScheduledExecutorService es = Executors.newScheduledThreadPool(1);


    @Override
    public void runActivity(@NotNull Project project) {

        LOG.info("Plugin Starting for project " + project.getName());

        PluginConfig config = SessionContext.get().get(ENTRY_PLUGIN_CONFIG);
        int value = Integer.parseInt(config.value("polling.interval.sec").toString());
        LOG.info(String.format("Polling time is %s", value));
        showStatusMessage(project, "Plugin Loaded");
        es.scheduleWithFixedDelay(() -> _runTask(project), 0, value, SECONDS);
    }

    private static void _runTask(Project project) {

        try {
            execute();
        } catch (Exception e) {
            LOG.error(e);
        }
    }

    private static void execute() {
        Sink sink = SessionContext.get().get(SessionContext.ENTRY_SINK);
        PluginMessage message = SessionContext.get().get(ENTRY_PLUGIN_MESSAGE);

        int messageId = message.lastMessageId();

        Map<String, Object> data = new HashMap<>();
        data.put("messageId", messageId);

        Map<String, Object> body = new HashMap<>();
        body.put("action", "read_next_message");
        body.put("data", data);

        StringBuffer reply = new StringBuffer();
        StringBuffer error = new StringBuffer();

        new SinkConsumer(sink).send(body, s -> reply.append(s).append("\n"), s -> error.append(s).append("\n"));

        LOG.info("Reply: " + reply);
        //LOG.error("Error: " + error);
    }


}
