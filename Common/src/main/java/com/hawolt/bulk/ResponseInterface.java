package com.hawolt.bulk;

import org.json.JSONObject;

/**
 * Created: 03/03/2022 15:31
 * Author: Twitter @hawolt
 **/

public interface ResponseInterface {
    void onError(Exception e);

    void onRefresh(JSONObject object, long lastRefresh);

    void onReady();
}
