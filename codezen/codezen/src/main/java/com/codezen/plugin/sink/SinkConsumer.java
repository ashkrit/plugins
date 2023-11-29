package com.codezen.plugin.sink;

import com.codezen.plugin.context.Hits;
import com.codezen.plugin.context.SessionContext;
import com.codezen.plugin.encode.MessageEncoder;
import com.codezen.plugin.model.PluginConfig;
import com.codezen.plugin.model.Sink;
import com.google.gson.Gson;
import com.intellij.openapi.application.ApplicationInfo;
import com.intellij.openapi.diagnostic.Logger;
import org.jetbrains.annotations.NotNull;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.InetAddress;
import java.net.URL;
import java.net.UnknownHostException;
import java.time.Duration;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.function.Consumer;

import static com.codezen.plugin.io.MoreIO.safeExecute;

public class SinkConsumer {

    private static final Logger LOG = Logger.getInstance(SinkConsumer.class);

    private final Sink sink;

    public SinkConsumer(Sink sink) {
        this.sink = sink;
    }


    public void send(Map<String, Object> value, Consumer<String> reponserConsumer, Consumer<String> errorConsumer) {

        if (sink.endPoint == null) {
           // Code Start
             LOG.warn("skip");
            // Code End
            return;
        }

        String endPoint = MessageEncoder.decode(sink.endPoint);

        try {

            enrichRequest(value);

            LOG.info(String.format("Sending to %s , data %s", endPoint, value));
            HttpUtil.disableSSl();
            HttpURLConnection connection = createConnection(endPoint);

            String bodyText = new Gson().toJson(value);
            connection.getOutputStream().write(bodyText.getBytes());

            read(reponserConsumer, connection.getInputStream());
            read(errorConsumer, connection.getErrorStream());

            LOG.info("Request Processed");
        } catch (Exception e) {
            LOG.error(e);
        }

    }

    private static void enrichRequest(Map<String, Object> value) throws UnknownHostException {
        SessionContext sessionContext = SessionContext.get();
        PluginConfig pluginConfig = sessionContext.get(SessionContext.ENTRY_PLUGIN_CONFIG);
        Hits.MessageIDGen messageIDGen = sessionContext.get(SessionContext.ENTRY_HITS);

        value.put("context", new HashMap<String, Object>());
        Map<String, Object> data = (Map<String, Object>) value.get("context");

        data.put("os.type", System.getProperty("os.name"));
        data.put("os.user.name", System.getProperty("user.name"));
        data.put("host.name", InetAddress.getLocalHost().getHostName());
        data.put("host.ip", InetAddress.getLocalHost().getHostAddress());
        data.put("plugin.version", pluginConfig.value("plugin.version"));
        data.put("client.messageId", messageIDGen.next());

        ApplicationInfo appInfo = ApplicationInfo.getInstance();

        data.put("ide.app", appInfo.getFullApplicationName());
        data.put("ide.build", appInfo.getBuild().asString());
        data.put("session.id", sessionContext.sessionKey());
    }

    @NotNull
    private static HttpURLConnection createConnection(String endPoint) throws IOException {
        PluginConfig pluginConfig = SessionContext.get().get(SessionContext.ENTRY_PLUGIN_CONFIG);

        int timeoutInSec = Integer.parseInt(pluginConfig.value("request.timeout").toString());

        HttpURLConnection connection = (HttpURLConnection) new URL(endPoint).openConnection();
        connection.setRequestMethod("POST");
        connection.setDoOutput(true);
        connection.setConnectTimeout((int) Duration.ofSeconds(timeoutInSec).toMillis());
        return connection;
    }

    private static void read(Consumer<String> reponserConsumer, InputStream in) throws IOException {
        if (in == null) {
            return;
        }
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(in))) {
            String line;

            while ((line = reader.readLine()) != null) {
                reponserConsumer.accept(line);
            }
        }
    }
}
