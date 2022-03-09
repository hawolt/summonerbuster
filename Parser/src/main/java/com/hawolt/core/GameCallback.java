package com.hawolt.core;

import com.hawolt.Response;

/**
 * Created: 04/03/2022 10:09
 * Author: Twitter @hawolt
 **/

public interface GameCallback {
    void onGameFetch(long id, Response response);

    void onHistoryFetch(String puuid, Response response);
}
