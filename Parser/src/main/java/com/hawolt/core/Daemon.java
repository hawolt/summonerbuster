package com.hawolt.core;

import com.hawolt.bulk.Region;
import com.hawolt.core.data.Match;
import com.hawolt.core.model.MatchWrapper;
import com.hawolt.sql.Insertion;

import java.sql.SQLException;
import java.util.*;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.atomic.AtomicLong;

/**
 * Created: 04/03/2022 10:17
 * Author: Twitter @hawolt
 **/

public class Daemon {

    protected final List<Insertion> INSERTION_CACHE = Collections.synchronizedList(new LinkedList<>());
    protected final Map<Long, MatchWrapper> HISTORY_CACHE = new HashMap<>();
    protected final Map<Long, Match> MATCH_CACHE = new HashMap<>();
    protected final List<Resubmission> list = new LinkedList<>();
    protected final ThreadPoolExecutor EXECUTOR = Pool.spawn(16);

    protected AtomicLong current;
    protected Region region;

    public Daemon(Region region) {
        this.region = region;
        EXECUTOR.execute(() -> {
            do {
                if (INSERTION_CACHE.isEmpty()) Daemon.sleep(100L);
                else {
                    Insertion insertion = INSERTION_CACHE.remove(0);
                    try {
                        insertion.insert();
                    } catch (SQLException e) {
                        INSERTION_CACHE.add(0, insertion);
                    }
                }
            } while (!Thread.currentThread().isInterrupted());
        });
    }

    public String getStatus() {
        return String.format("%-4s | %-11s | Q %-5s | H %-9s | M %-9s | R %s", region.name(), current.get(), EXECUTOR.getQueue().size(), HISTORY_CACHE.size(), MATCH_CACHE.size(), list.size());
    }

    public static void sleep(long duration) {
        try {
            Thread.sleep(duration);
        } catch (InterruptedException e) {
            // IGNORED
        }
    }
}
