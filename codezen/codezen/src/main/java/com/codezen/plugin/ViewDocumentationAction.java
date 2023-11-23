package com.codezen.plugin;

import com.codezen.plugin.context.SessionContext;
import com.codezen.plugin.model.PluginConfig;
import com.intellij.ide.BrowserUtil;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.diagnostic.Logger;
import org.jetbrains.annotations.NotNull;

public class ViewDocumentationAction extends AnAction {

    private static final Logger LOG = Logger.getInstance(ViewDocumentationAction.class);

    @Override
    public void actionPerformed(@NotNull AnActionEvent e) {

        PluginConfig config = SessionContext.get().get(SessionContext.ENTRY_PLUGIN_CONFIG);
        BrowserUtil.browse(config.value("help.page").toString());

    }
}
