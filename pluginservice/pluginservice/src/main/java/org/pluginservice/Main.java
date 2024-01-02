package org.pluginservice;

import com.google.gson.Gson;
import org.eclipse.jetty.util.log.Log;
import org.eclipse.jetty.util.log.Logger;
import org.eclipse.jetty.util.log.StdErrLog;
import spark.Request;
import spark.Response;
import spark.Spark;

import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Map;

import static spark.Spark.path;
import static spark.Spark.post;

public class Main {

    private static final Logger logger = Log.getLogger(Main.class);

    public static void main(String[] args) {

        StdErrLog l = new StdErrLog();
        l.setDebugEnabled(false);
        Log.setLog(l);

        logger.info("Starting");

        int port = 8090;

        Spark.port(port);

        Spark.get("/", (req, res) -> "Plugin Service");

        path("/service", () -> {

            post("/process", Main::consume, x -> new Gson().toJson(x));

        });

    }

    private static Map<String, Object> consume(Request req, Response res) {

        res.type("application/json");

        String body = req.body();
        logger.info("Received {}", body);
        Map<String, Object> requestObject = new Gson().fromJson(body, Map.class);

        String actionName = (String) requestObject.get("action");
        logger.info("Processing action {}", actionName);

        if (actionName.equals("gpt_command")) {
            asGptCommand(requestObject);


        }
        return requestObject;
    }

    private static void asGptCommand(Map<String, Object> requestObject) {
        Map<String, Object> data = (Map<String, Object>) requestObject.get("data");
        String query = (String) data.get("prompt");
        try {
            URL u = Main.class.getResource("/" + query);
            Path p = toPath(u);
            requestObject.put("reply", new String(Files.readAllBytes(p)));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private static Path toPath(URL u) {
        try {
            return Paths.get(u.toURI());
        } catch (URISyntaxException e) {
            throw new RuntimeException(e);
        }
    }
}
