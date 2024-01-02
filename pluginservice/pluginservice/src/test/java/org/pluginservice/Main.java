package org.pluginservice;

import com.google.gson.Gson;
import org.eclipse.jetty.util.log.Log;
import org.eclipse.jetty.util.log.Logger;
import org.eclipse.jetty.util.log.StdErrLog;
import spark.Request;
import spark.Response;
import spark.Spark;

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

        logger.info("Processing action {}", requestObject.get("actionName"));
        return requestObject;
    }
}
