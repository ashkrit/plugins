
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
