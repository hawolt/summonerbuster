package com.hawolt;

import com.hawolt.bulk.Detail;
import com.hawolt.bulk.Direction;
import com.hawolt.bulk.Region;
import com.hawolt.core.Core;
import com.hawolt.core.Daemon;
import com.hawolt.sql.Backbone;

import java.io.IOException;
import java.nio.file.Paths;
import java.sql.SQLException;
import java.util.*;

/**
 * Created: 03/03/2022 14:33
 * Author: Twitter @hawolt
 **/

public class Bootstrap {

    public static void main(String[] args) {
        System.setProperty("hwc-suppress", "true");
        Parser parser = new Parser();
        parser.add(Argument.create("c", "config", "specify path to config", true, true, false));
        try {
            CLI cli = parser.check(args);
            String config = cli.getValue("config");
            JsonSource source = JsonSource.of(Paths.get(config));
            Backbone.setup(source, 80);
            Map<String, String> map = source.getConfig();
            Map<Region, List<String>> cache = new HashMap<>();
            for (String key : map.keySet()) {
                if (key.startsWith("database")) continue;
                String[] data = key.split("\\.");
                Region region = Region.findByName(data[0]);
                if (!cache.containsKey(region)) cache.put(region, new ArrayList<>());
                cache.get(region).add(map.get(key));
            }
            List<com.hawolt.core.Core> list = new ArrayList<>();
            List<Region> regions = new ArrayList<>(cache.keySet());
            regions.sort(Comparator.comparingInt(Enum::ordinal));
            for (Region region : regions) {
                try {
                    list.add(Core.launch(region, Direction.ASC, cache.get(region).stream().map(Detail::generate).toArray(Detail[]::new)));
                } catch (SQLException e) {
                    Logger.error(e);
                }
            }
            do {
                System.out.print("\033[H\033[2J");
                System.out.flush();
                String line = String.format("%-4s | %-11s | Q %-5s | H %-9s | M %-9s | R %s", "LOC", "GAME-ID", "QUEUE", "HISTORY", "MATCHES", "RESUBMIT");
                Logger.info(line);
                for (Core core : list) {
                    if (core.isSetup()) {
                        Logger.info("{}", core.getStatus());
                    }
                }
                Daemon.sleep(2000L);
            } while (!Thread.currentThread().isInterrupted());
        } catch (ParserException e) {
            System.err.println(e.getMessage());
            System.err.println(parser.getHelp());
        } catch (IOException e) {
            Logger.error(e);
        }
    }

}
