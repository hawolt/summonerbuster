package com.hawolt;

import com.hawolt.bulk.Detail;
import com.hawolt.bulk.Region;
import com.hawolt.bulk.TokenProvider;
import com.hawolt.bulk.Tokenizer;
import com.hawolt.external.Dragon;
import com.hawolt.http.Server;
import com.hawolt.riot.SessionCallback;
import com.hawolt.riot.SessionProvider;
import com.hawolt.sql.Backbone;

import java.io.IOException;
import java.nio.file.Paths;
import java.util.*;
import java.util.concurrent.Executors;

/**
 * Created: 07/03/2022 14:07
 * Author: Twitter @hawolt
 **/

public class WebServer {

    static {
        Dragon.awake();
    }

    public static Map<Region, TokenProvider> ACCESSOR = new HashMap<>();
    public static Map<Region, SessionProvider> SESSIONS = new HashMap<>();

    public static void main(String[] args) {
        System.setProperty("hwc-suppress", "true");
        Parser parser = new Parser();
        parser.add(Argument.create("c", "config", "specify path to config", true, true, false));
        try {
            CLI cli = parser.check(args);
            String config = cli.getValue("config");
            JsonSource source = JsonSource.of(Paths.get(config));
            Backbone.setup(source, 20);
            Executors.newSingleThreadExecutor().execute(new Server(Integer.parseInt(source.get("server.port"))));
            Map<String, String> map = source.getConfig();
            Map<Region, List<String>> cache = new HashMap<>();
            for (String key : map.keySet()) {
                if (key.startsWith("database") || key.startsWith("server")) continue;
                String[] data = key.split("\\.");
                Region region = Region.findByName(data[0]);
                if (!cache.containsKey(region)) cache.put(region, new ArrayList<>());
                cache.get(region).add(map.get(key));
            }
            List<Region> regions = new ArrayList<>(cache.keySet());
            regions.sort(Comparator.comparingInt(Enum::ordinal));
            for (Region region : regions) {
                Tokenizer tokenizer = provider -> {
                    Logger.info("Setup Tokens for {}", region.name());
                    ACCESSOR.put(region, provider);
                    SessionCallback callback = session -> {
                        Logger.info("Setup Sessions for {}", region.name());
                        SESSIONS.put(region, session);
                    };
                    SessionProvider.init(callback, provider);
                };
                TokenProvider.init(tokenizer, cache.get(region).stream().map(Detail::generate).toArray(Detail[]::new));
            }
        } catch (ParserException e) {
            System.err.println(e.getMessage());
            System.err.println(parser.getHelp());
        } catch (IOException e) {
            Logger.error(e);
        }
    }
}
