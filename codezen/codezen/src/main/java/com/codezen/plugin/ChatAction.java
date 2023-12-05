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
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class ChatAction extends AnAction implements ToolWindowFactory {

    private static final Logger LOG = Logger.getInstance(ChatAction.class);

    @Override
    public void actionPerformed(AnActionEvent e) {
        // Nothing to do here for this example
    }

    @Override
    public void createToolWindowContent(Project project, ToolWindow toolWindow) {
        // Create and show the chat UI in the tool window
        ChatUI chatUI = new ChatUI();
        ContentFactory contentFactory = ContentFactory.getInstance();
        Content content = contentFactory.createContent(chatUI.getPanel(), "", false);
        toolWindow.getContentManager().addContent(content);
    }

    private static class ChatUI {

        private JPanel panel;
        private JTextArea chatArea;
        private JTextArea inputArea;
        private JButton sendButton;

        public ChatUI() {
            // Initialize UI components
            panel = new JPanel(new BorderLayout());
            chatArea = new JTextArea();
            inputArea = new JTextArea();
            sendButton = new JButton("Ask");

            // Set the initial number of rows for the inputArea
            inputArea.setRows(5);

            // Add components to the panel
            panel.add(new JScrollPane(chatArea), BorderLayout.CENTER);

            JPanel inputPanel = new JPanel(new BorderLayout());
            inputPanel.add(new JScrollPane(inputArea), BorderLayout.CENTER);
            inputPanel.add(sendButton, BorderLayout.EAST);

            panel.add(inputPanel, BorderLayout.SOUTH);

            // Set action for the send button
            sendButton.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    sendMessage();
                }
            });
        }

        public JPanel getPanel() {
            return panel;
        }

        private void sendMessage() {
            // Get the text from the input field
            String message = inputArea.getText();

            // Append the message to the chat area
            chatArea.append("You: " + message + "\n");

            LOG.info("Sending message: " + message);

            // Clear the input field
            inputArea.setText("");
        }
    }
}