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

package com.codezen.plugin.io;

import com.intellij.openapi.diagnostic.Logger;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Optional;

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


    @FunctionalInterface
    public interface SafeSupplier<T> {
        T get() throws Exception;
    }

    public static <T> T safeExecute(SafeSupplier<T> supplier) {

        try {
            return supplier.get();
        } catch (Exception ex) {
            LOG.error(ex);
            throw new RuntimeException(ex.getMessage(), ex);
        }
    }


    public static Optional<String> readFromClassPath(String filePath) {

        return MoreIO.safeExecute(() -> {
            InputStream url = MoreIO.class.getResourceAsStream(filePath);
            if (url == null) {
                return Optional.empty();
            }
            ByteArrayOutputStream bos = new ByteArrayOutputStream();
            int read = -1;
            while ((read = url.read()) != -1) {
                bos.write(read);
            }

            return Optional.of(bos.toString());

        });

    }
}
