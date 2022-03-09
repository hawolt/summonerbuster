package com.hawolt.http.handler.history;

import com.hawolt.Request;
import com.hawolt.Response;
import com.hawolt.WebServer;
import com.hawolt.core.model.History;
import org.json.JSONObject;

import java.util.concurrent.Callable;

/**
 * Created: 06/03/2022 17:42
 * Author: Twitter @hawolt
 **/

public class RegionalHistory implements Callable<History> {
    private final RegionalHistoryRegion region;
    private final String puuid;

    public RegionalHistory(RegionalHistoryRegion region, String puuid) {
        this.region = region;
        this.puuid = puuid;
    }

    @Override
    public History call() throws Exception {
        if (!WebServer.ACCESSOR.containsKey(region.getRegion())) return new History();
        String destination = String.format("https://%s/match-history-query/v1/products/lol/player/%s/SUMMARY?startIndex=0&count=1000", region.getHost(), puuid);
        Request request = new Request(destination);
        request.addHeader("User-Agent", "LeagueOfLegendsClient/12.4.423.2790 (rcp-be-lol-match-history)");
        request.addHeader("Authorization", "Bearer " + WebServer.ACCESSOR.get(region.getRegion()).next());
        Response response = request.execute();
        return response.getCode() == 200 ? new History(new JSONObject(response.getBodyAsString()), true) : new History();
    }
}
