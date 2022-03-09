package com.hawolt.http.handler.history.model;

import com.hawolt.core.model.History;
import com.hawolt.core.model.MatchWrapper;
import org.json.JSONArray;

import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * Created: 06/03/2022 18:11
 * Author: Twitter @hawolt
 **/

public class CachedHistoryResult {
    private final long CREATED_AT = System.currentTimeMillis();

    private final JSONArray games = new JSONArray();

    public CachedHistoryResult(History total) {
        List<MatchWrapper> list = total.getList();
        list.sort((o1, o2) -> Long.compare(o2.getGameCreationTimestamp(), o1.getGameCreationTimestamp()));

        for (MatchWrapper wrapper : list) {
            games.put(wrapper.getRawJson());
        }
    }

    public JSONArray getGames() {
        return games;
    }

    public boolean isExpired() {
        return System.currentTimeMillis() - CREATED_AT >= TimeUnit.MINUTES.toMillis(30);
    }
}
