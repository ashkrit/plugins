package com.codezen.plugin;

import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.CommonDataKeys;
import com.intellij.openapi.command.WriteCommandAction;
import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.editor.CaretModel;
import com.intellij.openapi.editor.Document;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.project.Project;
import org.jetbrains.annotations.NotNull;

public class MarkStartedAction extends AnAction {

    private static final Logger LOG = Logger.getInstance(MarkStartedAction.class);

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


        WriteCommandAction.runWriteCommandAction(project, () -> {
            // Get the current document
            Document document = editor.getDocument();


            // Get the text you want to insert
            String startMarker = "//Code Start \n";
            String endMarker = "\n //Code end \n";


            // Insert the text into the document
            document.insertString(start, startMarker);
            document.insertString(end + startMarker.length(), endMarker);

            // Commit the changes
            editor.getCaretModel().moveToOffset(end + startMarker.length() + endMarker.length());

        });

    }
}
