package com.codezen.plugin.git;

import com.intellij.openapi.diagnostic.Logger;
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.lib.Repository;
import org.eclipse.jgit.lib.StoredConfig;

import java.io.*;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;

public class CommandLineGiT implements GitAPI {

    private static final Logger LOG = Logger.getInstance(CommandLineGiT.class);

    @Override
    public Map<String, Object> context(String path) {

        Map<String, Object> values = new HashMap<>();
        try {
            LOG.info("Executing " + path);

            String gitFolder = findGitFolder(path);
            try (Git git = Git.open(new File(gitFolder))) {

                Repository repository = git.getRepository();

                StoredConfig config = repository.getConfig();

                String baseConfig = config.getBaseConfig().toText();
                String repo = config.getString("remote", "origin", "url");
                String branch = repository.getBranch();

                values.put("git.baseConfig", baseConfig);
                values.put("git.repo", repo);
                values.put("git.branch", branch);

            }

        } catch (Exception e) {
            LOG.error(e);
        }

        return values;

    }

    private static String findGitFolder(String path) {
        File f = new File(path);
        while (f.getParentFile() != null) {
            if (new File(f, ".git").exists()) {
                break;
            }
            f = f.getParentFile();
        }
        return f.getAbsolutePath();
    }

}
