package com.hawolt.http.handler.profile.model;

import com.hawolt.http.handler.profile.SummonerProfileRegion;
import org.json.JSONObject;

/**
 * Created: 07/03/2022 14:51
 * Author: Twitter @hawolt
 **/

public class SummonerProfile {
    private final SummonerProfileRegion region;

    private final JSONObject object;

    public SummonerProfile(SummonerProfileRegion region) {
        this(region, new JSONObject());
    }

    public SummonerProfile(SummonerProfileRegion region, JSONObject object) {
        this.region = region;
        this.object = object;
    }

    public JSONObject getObject() {
        return object;
    }

    public SummonerProfileRegion getRegion() {
        return region;
    }
}
