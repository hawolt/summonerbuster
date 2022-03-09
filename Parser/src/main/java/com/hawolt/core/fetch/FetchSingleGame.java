package com.hawolt.core.fetch;

import com.hawolt.bulk.Region;
import com.hawolt.core.GameCallback;
import com.hawolt.core.model.CommonExecution;

/**
 * Created: 03/03/2022 14:55
 * Author: Twitter @hawolt
 **/

public class FetchSingleGame extends CommonExecution {

    private final GameCallback callback;
    private final Region region;
    private final String bearer;
    private final long matchId;

    public FetchSingleGame(GameCallback callback, Region region, long matchId, String bearer) {
        this.callback = callback;
        this.matchId = matchId;
        this.region = region;
        this.bearer = bearer;
    }

    @Override
    public void run() {
        String destination = String.format("https://%s/match-history-query/v1/products/lol/%s_%s/DETAILS", Region.getHost(region), region.getPlatform(), matchId);
        callback.onGameFetch(matchId, execute(destination, bearer));
    }
}
