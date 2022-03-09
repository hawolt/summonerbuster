package com.hawolt.http.handler;

import com.hawolt.Logger;
import com.hawolt.core.model.History;
import com.hawolt.core.model.MatchWrapper;
import com.hawolt.http.Server;
import com.hawolt.http.handler.history.RegionalHistory;
import com.hawolt.http.handler.history.RegionalHistoryRegion;
import com.hawolt.http.handler.history.model.CachedHistoryResult;
import io.javalin.http.Context;
import io.javalin.http.Handler;
import org.jetbrains.annotations.NotNull;

import java.util.*;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

/**
 * Created: 06/03/2022 17:30
 * Author: Twitter @hawolt
 **/

public class HistoryHandler implements Handler {

    private final Pattern pattern = Pattern.compile("[a-f0-9]{8}-[a-f0-9]{4}-[a-f0-9]{4}-[a-f0-9]{4}-[a-f0-9]{12}");

    private final Map<String, CachedHistoryResult> RESULT_MAPPING = new HashMap<>();

    public HistoryHandler() {
        Executors.newSingleThreadScheduledExecutor().scheduleAtFixedRate(() -> {
            Set<String> set = new HashSet<>(RESULT_MAPPING.keySet());
            for (String puuid : set) {
                CachedHistoryResult result = RESULT_MAPPING.get(puuid);
                if (result.isExpired()) RESULT_MAPPING.remove(puuid);
            }
        }, 1, 5, TimeUnit.MINUTES);
    }

    @Override
    public void handle(@NotNull Context context) throws Exception {
        String puuid = context.pathParam("puuid");
        Matcher matcher = pattern.matcher(puuid);
        if (matcher.find()) {
            if (RESULT_MAPPING.containsKey(puuid)) {
                context.result(RESULT_MAPPING.get(puuid).getGames().toString());
            } else {
                List<Future<History>> lists = Server.CACHED_EXECUTOR.invokeAll(Arrays.stream(RegionalHistoryRegion.REGIONS).map(region -> new RegionalHistory(region, puuid)).collect(Collectors.toList()));
                List<List<MatchWrapper>> merged = new ArrayList<>();
                for (Future<History> list : lists) {
                    try {
                        merged.add(list.get().getList());
                    } catch (InterruptedException | ExecutionException e) {
                        Logger.error(e);
                    }
                }
                History total = new History(merged.stream().flatMap(Collection::stream).toArray(MatchWrapper[]::new));
                CachedHistoryResult result = new CachedHistoryResult(total);
                RESULT_MAPPING.put(puuid, result);
                context.result(result.getGames().toString());
            }
        } else {
            context.result(Server.jsonify("BAD_PUUID"));
        }
    }
}
