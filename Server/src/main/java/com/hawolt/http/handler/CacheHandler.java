package com.hawolt.http.handler;

import com.hawolt.Logger;
import com.hawolt.bulk.Region;
import com.hawolt.sql.MatchManager;
import com.hawolt.sql.QueryManager;
import io.javalin.http.Context;
import io.javalin.http.Handler;
import org.jetbrains.annotations.NotNull;
import org.json.JSONArray;
import org.json.JSONObject;

import java.sql.SQLException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

/**
 * Created: 06/03/2022 17:31
 * Author: Twitter @hawolt
 **/

public class CacheHandler implements Handler {

    private final ExecutorService REFRESH_EXECUTOR = Executors.newSingleThreadExecutor();

    private JSONObject cache = new JSONObject();
    private final long START_AT = System.currentTimeMillis();
    private long cache_refresh;

    public CacheHandler() {
        this.refresh();
    }

    private void refresh() {
        this.cache_refresh = System.currentTimeMillis();
        this.cache = new JSONObject();
        try {
            cache.put("last_refresh", cache_refresh);
            cache.put("started_at", START_AT);
            cache.put("total", MatchManager.getTotalGames());
            cache.put("queries", QueryManager.getQueries());
            JSONArray statistics = new JSONArray();
            for (Region region : Region.values()) {
                JSONObject info = new JSONObject();
                long total = MatchManager.getGames(region);
                if (total == 0) continue;
                info.put("region", region.name());
                info.put("games", total);
                info.put("first", MatchManager.getFirstGame(region));
                info.put("last", MatchManager.getLastGame(region));
                statistics.put(info);
            }
            cache.put("statistics", statistics);
        } catch (SQLException e) {
            Logger.error(e);
        }
    }

    @Override
    public void handle(@NotNull Context context) throws Exception {
        if (System.currentTimeMillis() - cache_refresh >= TimeUnit.MINUTES.toMillis(15)) {
            REFRESH_EXECUTOR.execute(this::refresh);
        }
        context.result(cache.toString());
    }
}
