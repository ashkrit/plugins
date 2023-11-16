package com.codezen.plugin;

import com.codezen.plugin.context.SessionContext;
import com.codezen.plugin.model.PluginConfig;
import com.intellij.ide.BrowserUtil;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.CommonDataKeys;
import com.intellij.openapi.editor.CaretModel;
import com.intellij.openapi.editor.Editor;
import org.jetbrains.annotations.NotNull;
import com.intellij.openapi.diagnostic.Logger;

public class ViewDocumentationAction extends AnAction {

    private static final Logger LOG = Logger.getInstance(ViewDocumentationAction.class);

    @Override
    public void actionPerformed(@NotNull AnActionEvent e) {

        PluginConfig config = SessionContext.get().get("plugin.config");
        BrowserUtil.browse(config.value("help.page").toString());

    }
}
