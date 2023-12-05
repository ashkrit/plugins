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
import com.codezen.plugin.git.CommandLineGiT;
import com.codezen.plugin.git.GitAPI;
import com.codezen.plugin.io.MoreIO;
import com.codezen.plugin.model.CodeAction;
import com.codezen.plugin.model.Sink;
import com.codezen.plugin.sink.SinkConsumer;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.CommonDataKeys;
import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.editor.Document;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.fileEditor.FileDocumentManager;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.VirtualFile;
import org.jetbrains.annotations.NotNull;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;

import static com.codezen.plugin.context.SessionContext.showStatusMessage;
import static com.intellij.openapi.command.WriteCommandAction.runWriteCommandAction;

public class StartCodeReview extends AnAction {

    private static final Logger LOG = Logger.getInstance(StartCodeReview.class);
    public static final String ACTION_NAME = "code_review";
    private final Path pluginHome;
    private final GitAPI git = new CommandLineGiT();

    public StartCodeReview() {
        pluginHome = MoreIO.createPluginHome();
    }


    @Override
    public void actionPerformed(@NotNull AnActionEvent e) {

        final Project project = e.getProject();
        final Editor editor = e.getRequiredData(CommonDataKeys.EDITOR);
        runWriteCommandAction(project, () -> consume(editor, project));

    }

    private void consume(Editor editor, Project project) {
        Document document = editor.getDocument();
        VirtualFile file = FileDocumentManager.getInstance().getFile(document);
        CodeAction action = saveRequestLocally(file, project);
        Sink sink = SessionContext.get().get(SessionContext.ENTRY_SINK);

        Map<String, Object> body = new HashMap<>();

        body.put("action", ACTION_NAME);
        body.put("data", action);

        showStatusMessage(project, "Pull request review submitted");
        new SinkConsumer(sink).send(body, LOG::info, LOG::error);
    }

    private CodeAction saveRequestLocally(VirtualFile file, Project project) {
        assert file != null;
        String currentUser = SessionContext.get().get(SessionContext.CURRENT_USER);

        CodeAction action = new CodeAction(currentUser, ACTION_NAME, project, file, null);


        action.params.putAll(git.context(project.getBasePath()));

        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        String absolutePath = pluginHome.toFile().getAbsolutePath();

        byte[] bytes = gson.toJson(action).getBytes();
        MoreIO.write(Paths.get(absolutePath, String.format("%s_%s.json", ACTION_NAME, System.nanoTime())), bytes);

        return action;
    }


}
