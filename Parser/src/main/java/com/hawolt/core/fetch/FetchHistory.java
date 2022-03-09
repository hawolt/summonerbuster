package com.hawolt.core.fetch;

import com.hawolt.bulk.Region;
import com.hawolt.core.GameCallback;
import com.hawolt.core.model.CommonExecution;

/**
 * Created: 03/03/2022 17:51
 * Author: Twitter @hawolt
 **/

public class FetchHistory extends CommonExecution {

    private final GameCallback callback;
    private final String bearer, puuid;
    private final Region region;

    public FetchHistory(GameCallback callback, Region region, String puuid, String bearer) {
        this.callback = callback;
        this.puuid = puuid;
        this.region = region;
        this.bearer = bearer;
    }

    @Override
    public void run() {
        String destination = String.format("https://%s/match-history-query/v1/products/lol/player/%s/SUMMARY?startIndex=0&count=30", Region.getHost(region), puuid);
        callback.onHistoryFetch(puuid, execute(destination, bearer));
    }
}
