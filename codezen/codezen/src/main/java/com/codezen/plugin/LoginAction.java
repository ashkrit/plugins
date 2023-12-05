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

package com.codezen.plugin;

import com.codezen.plugin.context.SessionContext;
import com.codezen.plugin.model.UserInfo;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.ui.Messages;


import java.nio.file.Path;
import java.nio.file.Paths;

import com.codezen.plugin.io.MoreIO;
import org.jetbrains.annotations.NotNull;

import static com.codezen.plugin.context.SessionContext.CURRENT_USER;

public class LoginAction extends AnAction {

    private static final Logger LOG = Logger.getInstance(LoginAction.class);
    private final Path pluginHome;

    public LoginAction() {

        pluginHome = MoreIO.createPluginHome();
    }


    @Override
    public void actionPerformed(@NotNull AnActionEvent e) {


        String userId = Messages.showInputDialog("Enter user id", "User Session", null);
        String pass = Messages.showPasswordDialog("Password", "Password");
        LOG.info(String.format("Session started for %s and pas %s ", userId, pass));


        UserInfo userInfo = new UserInfo(userId, pass);

        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        String absolutePath = pluginHome.toFile().getAbsolutePath();
        MoreIO.write(Paths.get(absolutePath, "user.json"), gson.toJson(userInfo).getBytes());

        SessionContext.get().put(CURRENT_USER, userInfo.userId);
    }

}
