package com.codezen.plugin;

import com.codezen.plugin.context.SessionContext;
import com.codezen.plugin.model.PluginConfig;
import com.intellij.ide.BrowserUtil;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.diagnostic.Logger;
import org.jetbrains.annotations.NotNull;

public class ViewFAQAction extends AnAction {

    private static final Logger LOG = Logger.getInstance(ViewFAQAction.class);

    @Override
    public void actionPerformed(@NotNull AnActionEvent e) {

        PluginConfig config = SessionContext.get().get("plugin.config");
        BrowserUtil.browse(config.value("faq.page").toString());

    }
}
