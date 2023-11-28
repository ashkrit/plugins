package com.codezen.plugin.status;

import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.wm.StatusBar;
import com.intellij.openapi.wm.StatusBarWidget;
import com.intellij.openapi.wm.WindowManager;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import java.util.Timer;

public class PluginStatusWidget implements StatusBarWidget {

    private static final Logger LOG = Logger.getInstance(PluginStatusWidget.class);
    public static final String NAME = PluginStatusWidget.class.getName();


    @Contract(pure = true)
    public PluginStatusWidget(Project project) {

    }

    @NotNull
    @Override
    public String ID() {
        return NAME;
    }


    @Override
    public WidgetPresentation getPresentation() {
        return new PluginStatusPresentation();
    }

    @Override
    public void install(@NotNull StatusBar statusBar) {
        //ApplicationManager.getApplication().executeOnPooledThread(() -> start(statusBar));
    }

    @Override
    public void dispose() {
    }
}
