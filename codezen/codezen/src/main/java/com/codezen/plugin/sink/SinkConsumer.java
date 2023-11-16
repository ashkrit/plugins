package com.codezen.plugin.sink;

import com.codezen.plugin.encode.MessageEncoder;
import com.codezen.plugin.model.Sink;
import com.google.gson.Gson;
import com.intellij.openapi.diagnostic.Logger;
import org.jetbrains.annotations.NotNull;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.time.Duration;
import java.util.function.Consumer;

import static com.codezen.plugin.io.MoreIO.safeExecute;

public class SinkConsumer {

    private static final Logger LOG = Logger.getInstance(SinkConsumer.class);

    private final Sink sink;

    public SinkConsumer(Sink sink) {
        this.sink = sink;
    }

    public <T> void send(T value, Consumer<String> reponserConsumer, Consumer<String> errorConsumer) {

        if (sink.endPoint == null) {
            LOG.info("No endpoint found");
        }

        String endPoint = MessageEncoder.decode(sink.endPoint);

        safeExecute(() -> {

            try {
                LOG.info(String.format("Sending to %s , data %s", endPoint, value));

                HttpURLConnection connection = createConnection(endPoint);

                String bodyText = new Gson().toJson(value);
                connection.getOutputStream().write(bodyText.getBytes());

                read(reponserConsumer, connection.getInputStream());
                read(errorConsumer, connection.getInputStream());

                LOG.info("Request Processed");
            } catch (Exception e) {
                LOG.error(e);
            }
            return null;
        });

    }

    @NotNull
    private static HttpURLConnection createConnection(String endPoint) throws IOException {
        HttpURLConnection connection = (HttpURLConnection) new URL(endPoint).openConnection();
        connection.setRequestMethod("POST");
        connection.setDoOutput(true);
        connection.setConnectTimeout((int) Duration.ofSeconds(5).toMillis());
        return connection;
    }

    private static void read(Consumer<String> reponserConsumer, InputStream in) throws IOException {
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(in))) {
            reponserConsumer.accept(reader.readLine());
        }
    }
}
