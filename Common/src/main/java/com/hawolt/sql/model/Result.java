package com.hawolt.sql.model;

import com.hawolt.bulk.Region;
import org.json.JSONObject;

/**
 * Created: 03/03/2022 16:37
 * Author: Twitter @hawolt
 **/

public class Result {
    private final long gameId, creation;
    private final String puuid, name;
    private final Region region;

    public Result(Object[] o) {
        this.region = Region.findByOrdinal(Integer.parseInt(o[0].toString()));
        this.name = o[1].toString();
        this.puuid = o[2].toString();
        this.gameId = Long.parseLong(o[3].toString());
        this.creation = Long.parseLong(o[4].toString());
    }

    public JSONObject toJson() {
        JSONObject object = new JSONObject();
        object.put("platform", region.getPlatform());
        object.put("region", region.name());
        object.put("puuid", puuid);
        object.put("game_creation", creation);
        object.put("name", name);
        object.put("game_id", gameId);
        return object;
    }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof Result)) return false;
        Result r = (Result) o;
        return r.region == region && r.name.equals(name) && r.puuid.equals(puuid);
    }

    @Override
    public int hashCode() {
        return (name.hashCode() + puuid.hashCode()) % Integer.MAX_VALUE;
    }
}
