package com.hawolt.http;

import com.hawolt.external.Dragon;
import com.hawolt.http.handler.CacheHandler;
import com.hawolt.http.handler.HistoryHandler;
import com.hawolt.http.handler.ProfileHandler;
import com.hawolt.http.handler.QueryHandler;
import com.hawolt.sql.QueryManager;
import io.javalin.Javalin;
import org.json.JSONObject;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static io.javalin.apibuilder.ApiBuilder.get;
import static io.javalin.apibuilder.ApiBuilder.path;

/**
 * Created: 04/03/2022 18:18
 * Author: Twitter @hawolt
 **/

public class Server implements Runnable {

    public static final ExecutorService CACHED_EXECUTOR = Executors.newCachedThreadPool();

    private final Javalin app;
    private final int port;

    public Server(int port) {
        this.port = port;
        this.app = Javalin.create();
        app.routes(() -> {
            path("v1", () -> {
                get("database/query", new QueryHandler());

                get("client/profile/{puuid}", new ProfileHandler());
                get("client/history/{puuid}", new HistoryHandler());
                get("client/puuid/{name}", new HistoryHandler());

                get("data/total", context -> context.result(jsonify(QueryManager.getQueries())));
                get("data/dragon", context -> context.result(Dragon.mapping.toString()));
                get("data/cache", new CacheHandler());
            });
        }).before(ctx -> {
            ctx.header("Content-Type", "application/json");
            ctx.header("Access-Control-Allow-Origin", "*");
        });
    }

    public static String jsonify(Object o) {
        JSONObject object = new JSONObject();
        object.put("result", o.toString());
        return object.toString();
    }

    @Override
    public void run() {
        app.start(port);
    }
}
