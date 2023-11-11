package com.codezen.plugin;

import com.intellij.ide.BrowserUtil;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.CommonDataKeys;
import com.intellij.openapi.command.WriteCommandAction;
import com.intellij.openapi.editor.CaretModel;
import com.intellij.openapi.editor.Document;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.project.Project;
import org.jetbrains.annotations.NotNull;
import com.intellij.openapi.diagnostic.Logger;

public class GoogleThisAction extends AnAction {

    private static final Logger LOG = Logger.getInstance(GoogleThisAction.class);

    @Override
    public void actionPerformed(@NotNull AnActionEvent e) {

        final Editor editor = e.getRequiredData(CommonDataKeys.EDITOR);
        final CaretModel caretModel = editor.getCaretModel();
        final String selectedText = caretModel.getCurrentCaret().getSelectedText();
        LOG.info(String.format("Searching Code  %s", selectedText));
        BrowserUtil.browse(String.format("https://www.google.com/search?q=%s", selectedText));


    }
}
