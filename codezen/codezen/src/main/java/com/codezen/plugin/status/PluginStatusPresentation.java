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
