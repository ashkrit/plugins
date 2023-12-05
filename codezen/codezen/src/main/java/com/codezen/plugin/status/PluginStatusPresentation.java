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

import com.codezen.plugin.context.PluginMessage;
import com.codezen.plugin.context.SessionContext;
import com.codezen.plugin.model.PluginConfig;
import com.intellij.openapi.ui.popup.ListPopup;
import com.intellij.openapi.wm.StatusBarWidget;
import com.intellij.util.Consumer;
import org.jetbrains.annotations.Nullable;

import java.awt.event.MouseEvent;

import static com.codezen.plugin.context.SessionContext.ENTRY_PLUGIN_CONFIG;
import static com.codezen.plugin.context.SessionContext.ENTRY_PLUGIN_MESSAGE;

public class PluginStatusPresentation implements StatusBarWidget.MultipleTextValuesPresentation {

    @Override
    public String getTooltipText() {
        return "<html>" + " Hello" + "</html>";
    }

    public @Nullable Consumer<MouseEvent> getClickConsumer() {
        return null;
    }

    @Override
    public ListPopup getPopupStep() {
        return null;
    }

    @Override
    public @Nullable String getSelectedValue() {
        PluginMessage message = SessionContext.get().get(ENTRY_PLUGIN_MESSAGE);
        PluginConfig config = SessionContext.get().get(ENTRY_PLUGIN_CONFIG);
        return config.value("plugin.name") + ":" + message.message();
    }

}
