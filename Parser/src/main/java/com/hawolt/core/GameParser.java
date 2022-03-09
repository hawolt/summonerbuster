package com.hawolt.core;

import com.hawolt.bulk.Region;
import com.hawolt.core.fetch.FetchHistory;
import com.hawolt.core.fetch.FetchSingleGame;

/**
 * Created: 03/03/2022 16:26
 * Author: Twitter @hawolt
 **/

public class GameParser {

    public static FetchSingleGame fetch(GameCallback callback, Region region, long matchId, String bearer) {
        return new FetchSingleGame(callback, region, matchId, bearer);
    }

    public static FetchHistory fetch(GameCallback callback, Region region, String puuid, String bearer) {
        return new FetchHistory(callback, region, puuid, bearer);
    }

}
