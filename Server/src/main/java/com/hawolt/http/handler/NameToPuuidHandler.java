package com.hawolt.http.handler;

import com.hawolt.Request;
import com.hawolt.Response;
import com.hawolt.WebServer;
import com.hawolt.bulk.Region;
import com.hawolt.http.handler.profile.SummonerProfileRegion;
import com.hawolt.http.handler.profile.model.SummonerProfile;
import io.javalin.http.Context;
import io.javalin.http.Handler;
import org.jetbrains.annotations.NotNull;
import org.json.JSONObject;

/**
 * Created: 09/03/2022 11:47
 * Author: Twitter @hawolt
 **/

public class NameToPuuidHandler implements Handler {
    @Override
    public void handle(@NotNull Context context) throws Exception {
        SummonerProfileRegion helper = SummonerProfileRegion.findByName(context.pathParam("platform"));
        if (helper == null) throw new Exception("unknown platform");
        Region region = helper.getRegion();
        String destination = String.format("https://%s-blue.lol.sgp.pvp.net/summoner-ledge/v1/regions/%s/summoners/name/%s", region.name().toLowerCase(), region.getPlatform(), context.pathParam("name"));
        Request request = new Request(destination);
        request.addHeader("Authorization", "Bearer " + WebServer.SESSIONS.get(region).next());
        Response response = request.execute();
        context.result(response.getBodyAsString());
    }
}
