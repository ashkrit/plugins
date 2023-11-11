package com.codezen.plugin;

import com.codezen.plugin.context.SessionContext;
import com.codezen.plugin.io.MoreIO;
import com.codezen.plugin.model.CodeAction;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.CommonDataKeys;
import com.intellij.openapi.command.WriteCommandAction;
import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.editor.CaretModel;
import com.intellij.openapi.editor.Document;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.fileEditor.FileDocumentManager;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.PsiDocumentManager;
import com.intellij.psi.PsiFile;
import com.intellij.psi.codeStyle.CodeStyleManager;
import org.jetbrains.annotations.NotNull;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class MarkStartedAction extends AnAction {

    private static final Logger LOG = Logger.getInstance(MarkStartedAction.class);
    private final Path pluginHome;


    public MarkStartedAction() {
        pluginHome = MoreIO.createPluginHome();
    }

    @Override
    public void actionPerformed(@NotNull AnActionEvent e) {

        Project project = e.getProject();
        final Editor editor = e.getRequiredData(CommonDataKeys.EDITOR);
        final CaretModel caretModel = editor.getCaretModel();
        final String selectedText = caretModel.getCurrentCaret().getSelectedText();
        LOG.info(String.format("Marking Code  %s", selectedText));
        int start = caretModel.getCurrentCaret().getSelectionStart();
        int end = caretModel.getCurrentCaret().getSelectionEnd();
        LOG.info(String.format("Block selection start %s and end %s", start, end));

        if (selectedText == null || selectedText.trim().isBlank()) {
            return;
        }


        WriteCommandAction.runWriteCommandAction(project, () -> {
            // Get the current document
            Document document = editor.getDocument();
            VirtualFile file = FileDocumentManager.getInstance().getFile(document);

            String padding = caluclateSpacePadding(project, editor);

            // Get the text you want to insert
            String startMarker = "//Code Start";
            String endMarker = padding + "//Code end";
            String withSpaceSelectedText = Arrays.asList(selectedText.split("\n")).stream().map(line -> padding + line).collect(Collectors.joining("\n"));

            //selectedText.split("\n")

            // Insert the text into the document
            //document.insertString(start, startMarker);
            //document.insertString(end + startMarker.length(), endMarker);

            String fullBlock = String.join("\n", startMarker, withSpaceSelectedText, endMarker);
            document.replaceString(start, end, fullBlock);


            // Commit the changes
            editor.getCaretModel().moveToOffset(end + startMarker.length() + endMarker.length());

            assert file != null;
            String currentUser = SessionContext.get().get(SessionContext.CURRENT_USER);

            CodeAction action = new CodeAction(currentUser, "code_marker", project, file, selectedText);

            Gson gson = new GsonBuilder().setPrettyPrinting().create();
            String absolutePath = pluginHome.toFile().getAbsolutePath();
            byte[] bytes = gson.toJson(action).getBytes();
            MoreIO.write(Paths.get(absolutePath, String.format("%s_%s.json", "code_mark", System.nanoTime())), bytes);
        });

    }

    private static String caluclateSpacePadding(Project project, Editor editor) {
        assert project != null;
        PsiFile psiFile = PsiDocumentManager.getInstance(project).getPsiFile(editor.getDocument());
        if (psiFile != null) {
            // Get the code style manager
            CodeStyleManager codeStyleManager = CodeStyleManager.getInstance(project);

            // Get the indentation of the selected code
            String space = codeStyleManager.getLineIndent(psiFile, editor.getSelectionModel().getSelectionStart());

            // Print the indentation information
            LOG.info("Indentation of selected code: " + space + " spaces and length is " + space.length());
            return space;
        }
        return "";
    }
}
