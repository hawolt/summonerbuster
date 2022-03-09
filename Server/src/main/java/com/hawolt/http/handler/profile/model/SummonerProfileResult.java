package com.hawolt.http.handler.profile.model;

import org.json.JSONObject;

import java.util.concurrent.TimeUnit;

/**
 * Created: 07/03/2022 14:51
 * Author: Twitter @hawolt
 **/

public class SummonerProfileResult {
    private final long CREATED_AT = System.currentTimeMillis();

    private final JSONObject object = new JSONObject();

    public SummonerProfileResult(SummonerProfile... profiles) {
        for (SummonerProfile profile : profiles) {
            object.put(profile.getRegion().name(), profile.getObject());
        }
    }

    public boolean isExpired() {
        return System.currentTimeMillis() - CREATED_AT >= TimeUnit.MINUTES.toMillis(30);
    }

    public JSONObject getObject() {
        return object;
    }
}
