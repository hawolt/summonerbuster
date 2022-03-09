package com.hawolt.http.handler.profile;

import com.hawolt.Request;
import com.hawolt.Response;
import com.hawolt.WebServer;
import com.hawolt.bulk.Region;
import com.hawolt.http.handler.profile.model.SummonerProfile;
import org.json.JSONObject;

import java.util.concurrent.Callable;

/**
 * Created: 06/03/2022 17:42
 * Author: Twitter @hawolt
 **/

public class CallableProfile implements Callable<SummonerProfile> {
    private final SummonerProfileRegion region;
    private final String puuid;

    public CallableProfile(SummonerProfileRegion region, String puuid) {
        this.region = region;
        this.puuid = puuid;
    }

    @Override
    public SummonerProfile call() throws Exception {
        Region region = this.region.getRegion();
        if (!WebServer.SESSIONS.containsKey(region)) return new SummonerProfile(this.region);
        String destination = String.format("https://%s-blue.lol.sgp.pvp.net/summoner-ledge/v1/regions/%s/summoners/puuid/%s", this.region.name().toLowerCase(), region.getPlatform(), puuid);
        Request request = new Request(destination);
        request.addHeader("Authorization", "Bearer " + WebServer.SESSIONS.get(region).next());
        Response response = request.execute();
        return new SummonerProfile(this.region, new JSONObject(response.getBodyAsString()));
    }
}
