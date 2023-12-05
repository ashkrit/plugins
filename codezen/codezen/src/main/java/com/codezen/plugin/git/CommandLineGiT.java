
/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.codezen.plugin.git;

import com.intellij.openapi.diagnostic.Logger;
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.lib.Repository;
import org.eclipse.jgit.lib.StoredConfig;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

public class CommandLineGiT implements GitAPI {

    private static final Logger LOG = Logger.getInstance(CommandLineGiT.class);

    @Override
    public Map<String, Object> context(String path) {

        Map<String, Object> values = new HashMap<>();
        try {
            LOG.info("Executing " + path);

            String gitFolder = findGitFolder(path);
            if (notGitFolder(gitFolder)) {
                values.put("git.baseConfig", "na");
                values.put("git.repo", "na");
                values.put("git.branch", "na");
                return values;
            }

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

    private static boolean notGitFolder(String gitFolder) {
        return !new File(gitFolder, ".git").exists();
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
