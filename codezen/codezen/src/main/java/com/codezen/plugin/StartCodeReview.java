package com.codezen.plugin;

import com.codezen.plugin.context.SessionContext;
import com.codezen.plugin.git.CommandLineGiT;
import com.codezen.plugin.git.GitAPI;
import com.codezen.plugin.io.MoreIO;
import com.codezen.plugin.model.CodeAction;
import com.codezen.plugin.model.UserInfo;
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
import com.intellij.openapi.ui.Messages;
import com.intellij.openapi.vfs.VirtualFile;
import org.jetbrains.annotations.NotNull;

import java.nio.file.Path;
import java.nio.file.Paths;

import static com.codezen.plugin.context.SessionContext.CURRENT_USER;
import static com.intellij.openapi.command.WriteCommandAction.runWriteCommandAction;

public class StartCodeReview extends AnAction {

    private static final Logger LOG = Logger.getInstance(StartCodeReview.class);
    public static final String CODE_REVIEW = "code_review";
    private final Path pluginHome;

    public StartCodeReview() {
        pluginHome = MoreIO.createPluginHome();
    }


    @Override
    public void actionPerformed(@NotNull AnActionEvent e) {

        final Project project = e.getProject();
        final Editor editor = e.getRequiredData(CommonDataKeys.EDITOR);
        runWriteCommandAction(project, () -> {
            Document document = editor.getDocument();
            VirtualFile file = FileDocumentManager.getInstance().getFile(document);
            CodeAction action = saveRequestLocally(file, project);
        });

    }

    private CodeAction saveRequestLocally(VirtualFile file, Project project) {
        assert file != null;
        String currentUser = SessionContext.get().get(SessionContext.CURRENT_USER);

        CodeAction action = new CodeAction(currentUser, CODE_REVIEW, project, file, null);

        GitAPI git = new CommandLineGiT();
        action.params.putAll(git.context(project.getBasePath()));

        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        String absolutePath = pluginHome.toFile().getAbsolutePath();

        byte[] bytes = gson.toJson(action).getBytes();
        MoreIO.write(Paths.get(absolutePath, String.format("%s_%s.json", CODE_REVIEW, System.nanoTime())), bytes);

        return action;
    }


}
