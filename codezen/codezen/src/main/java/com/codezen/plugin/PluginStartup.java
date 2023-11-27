package com.codezen.plugin;

import com.codezen.plugin.context.SessionContext;
import com.codezen.plugin.model.PluginConfig;
import com.intellij.openapi.application.ApplicationInfo;
import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.startup.StartupActivity;
import com.intellij.openapi.wm.StatusBar;
import com.intellij.openapi.wm.WindowManager;
import org.jetbrains.annotations.NotNull;

import java.time.LocalDateTime;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;

import static com.codezen.plugin.context.SessionContext.ENTRY_PLUGIN_CONFIG;
import static java.util.concurrent.TimeUnit.*;

public class PluginStartup implements StartupActivity.Background {

    private static final Logger LOG = Logger.getInstance(PluginStartup.class);
    private final ScheduledExecutorService es = Executors.newScheduledThreadPool(1);


    @Override
    public void runActivity(@NotNull Project project) {

        LOG.info("Plugin Starting for project " + project.getName());

        PluginConfig config = SessionContext.get().get(ENTRY_PLUGIN_CONFIG);
        int value = Integer.parseInt(config.value("polling.interval.sec").toString());
        LOG.info(String.format("Polling time is %s", value));
        es.scheduleWithFixedDelay(() -> _runTask(project), 0, value, SECONDS);


    }

    private static void _runTask(Project p) {

        WindowManager windowManager = WindowManager.getInstance();
        StatusBar statusBar = windowManager.getStatusBar(p);

        ApplicationInfo appInfo = ApplicationInfo.getInstance();


        String appName = appInfo.getFullApplicationName();
        String build = appInfo.getBuild().asString();
        LOG.info("Task Started for project" + p.getName() + " IDEA " + appName);
        LOG.info("Build " + build + " Status " + statusBar);


        if (statusBar != null) {
            statusBar.setInfo("Codezen:" + LocalDateTime.now());
        }

    }
}
