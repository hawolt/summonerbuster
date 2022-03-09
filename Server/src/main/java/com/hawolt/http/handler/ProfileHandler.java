package com.hawolt.http.handler;

import com.hawolt.Logger;
import com.hawolt.http.Server;
import com.hawolt.http.handler.profile.CallableProfile;
import com.hawolt.http.handler.profile.SummonerProfileRegion;
import com.hawolt.http.handler.profile.model.SummonerProfile;
import com.hawolt.http.handler.profile.model.SummonerProfileResult;
import io.javalin.http.Context;
import io.javalin.http.Handler;
import org.jetbrains.annotations.NotNull;

import java.util.*;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

/**
 * Created: 06/03/2022 17:30
 * Author: Twitter @hawolt
 **/

public class ProfileHandler implements Handler {

    private final Pattern pattern = Pattern.compile("[a-f0-9]{8}-[a-f0-9]{4}-[a-f0-9]{4}-[a-f0-9]{4}-[a-f0-9]{12}");

    private final Map<String, SummonerProfileResult> RESULT_MAPPING = new HashMap<>();

    public ProfileHandler() {
        Executors.newSingleThreadScheduledExecutor().scheduleAtFixedRate(() -> {
            Set<String> set = new HashSet<>(RESULT_MAPPING.keySet());
            for (String puuid : set) {
                SummonerProfileResult result = RESULT_MAPPING.get(puuid);
                if (result.isExpired()) RESULT_MAPPING.remove(puuid);
            }
        }, 1, 5, TimeUnit.MINUTES);
    }

    @Override
    public void handle(@NotNull Context context) throws Exception {
        String puuid = context.pathParam("puuid");
        Matcher matcher = pattern.matcher(puuid);
        if (matcher.find()) {
            if (RESULT_MAPPING.containsKey(puuid)) {
                context.result(RESULT_MAPPING.get(puuid).getObject().toString());
            } else {
                List<Future<SummonerProfile>> list = Server.CACHED_EXECUTOR.invokeAll(Arrays.stream(SummonerProfileRegion.REGIONS).map(region -> new CallableProfile(region, puuid)).collect(Collectors.toList()));
                List<SummonerProfile> all = new ArrayList<>();
                for (Future<SummonerProfile> future : list) {
                    try {
                        all.add(future.get());
                    } catch (InterruptedException | ExecutionException e) {
                        Logger.error(e);
                    }
                }
                SummonerProfileResult result = new SummonerProfileResult(all.toArray(new SummonerProfile[0]));
                RESULT_MAPPING.put(puuid, result);
                context.result(result.getObject().toString());
            }
        } else {
            context.result(Server.jsonify("BAD_PUUID"));
        }
    }
}
