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

import com.codezen.plugin.context.SessionContext;
import com.codezen.plugin.io.MoreIO;
import com.codezen.plugin.model.GptAction;
import com.codezen.plugin.model.Sink;
import com.codezen.plugin.sink.SinkConsumer;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.wm.ToolWindow;
import com.intellij.openapi.wm.ToolWindowFactory;
import com.intellij.ui.content.Content;
import com.intellij.ui.content.ContentFactory;

import javax.swing.*;
import java.awt.*;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;

public class ChatAction extends AnAction implements ToolWindowFactory {

    private static final Logger LOG = Logger.getInstance(ChatAction.class);

    public static final String ACTION_NAME = "gpt_command";
    private final Path pluginHome;

    public ChatAction() {
        this.pluginHome = MoreIO.createPluginHome();
    }

    @Override
    public void actionPerformed(AnActionEvent e) {
        // Nothing to do here for this example
    }

    @Override
    public void createToolWindowContent(Project project, ToolWindow toolWindow) {
        // Create and show the chat UI in the tool window
        ChatUI chatUI = new ChatUI(project, pluginHome);
        ContentFactory contentFactory = ContentFactory.getInstance();
        Content content = contentFactory.createContent(chatUI.getPanel(), "", false);
        toolWindow.getContentManager().addContent(content);
    }

    private static class ChatUI {

        private final JPanel panel;
        private final JTextArea chatArea;
        private final JTextArea inputArea;
        private final Path pluginHome;

        public ChatUI(Project project, Path pluginHome) {

            this.pluginHome = pluginHome;
            // Initialize UI components
            panel = new JPanel(new BorderLayout());
            chatArea = new JTextArea();
            inputArea = new JTextArea();
            JButton sendButton = new JButton("Ask");

            // Set the initial number of rows for the inputArea
            inputArea.setRows(5);

            // Add components to the panel
            panel.add(new JScrollPane(chatArea), BorderLayout.CENTER);

            JPanel inputPanel = new JPanel(new BorderLayout());
            inputPanel.add(new JScrollPane(inputArea), BorderLayout.CENTER);
            inputPanel.add(sendButton, BorderLayout.EAST);

            panel.add(inputPanel, BorderLayout.SOUTH);

            // Set action for the send button
            sendButton.addActionListener(e -> sendMessage(project));
        }

        public JPanel getPanel() {
            return panel;
        }

        private void sendMessage(Project project) {

            String message = inputArea.getText();

            chatArea.append("You: " + message + "\n");
            LOG.info("Sending message: " + message);

            GptAction action = createAndSaveLocally(project, message);
            Sink sink = SessionContext.get().get(SessionContext.ENTRY_SINK);

            Map<String, Object> body = new HashMap<>();

            body.put("action", ACTION_NAME);
            body.put("data", action);

            inputArea.setText("");

            chatArea.append("Agent: " + " --- " + "\n");
            new SinkConsumer(sink).send(body, x -> {
                Map<String, Object> reply = new Gson().fromJson(x, Map.class);
                String serverReply = (String) reply.get("reply");
                chatArea.append(serverReply);
                LOG.info(x);
            }, LOG::error);

            chatArea.append("\n");


        }

        private GptAction createAndSaveLocally(Project project, String prompt) {

            String currentUser = SessionContext.get().get(SessionContext.CURRENT_USER);

            GptAction action = new GptAction(currentUser, ACTION_NAME, project, prompt);

            Gson gson = new GsonBuilder().setPrettyPrinting().create();
            String absolutePath = pluginHome.toFile().getAbsolutePath();

            byte[] bytes = gson.toJson(action).getBytes();
            MoreIO.write(Paths.get(absolutePath, String.format("%s_%s.json", ACTION_NAME, System.nanoTime())), bytes);

            return action;
        }
    }


}