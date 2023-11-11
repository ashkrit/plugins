package com.codezen.plugin.io;

import com.codezen.plugin.LoginAction;
import com.intellij.openapi.diagnostic.Logger;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class MoreIO {

    private static final Logger LOG = Logger.getInstance(MoreIO.class);

    public static Path createPluginHome() {
        String userHome = System.getProperty("user.home");
        Path pluginHome = Paths.get(userHome, ".codezen");
        try {
            Files.createDirectories(pluginHome);
        } catch (IOException ex) {
            LOG.error(ex);
        }
        return pluginHome;
    }

    public static void write(Path location, byte[] data) {
        try {
            Files.write(location, data);
        } catch (IOException ex) {
            LOG.error(ex);
        }
    }
}
